package com.ayata.clad.product.qa

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentQABinding
import com.ayata.clad.product.adapter.AdapterQaMultiple
import com.ayata.clad.profile.faq.viewmodel.FAQViewModel
import com.ayata.clad.profile.faq.viewmodel.FAQViewModelFactory
import com.ayata.clad.utils.Caller
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.ProgressDialog
import com.google.gson.JsonObject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
private const val ARG_PRODUCTID = "param3"
private const val ARG_BRAND = "param4"


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentQA.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentQA : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: MutableList<ModelQA>? = null

    //    private var param2: Int? = null
    private var productId: Int? = null
    private var brand: String? = null
    private lateinit var binding: FragmentQABinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var viewModel: FAQViewModel
    private val myqaAdapter by lazy { AdapterQaMultiple() }
    private var isFromEdit: ModelQA.Question? = null
    private var editPosition:Int=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        arguments?.let {
//            param1 = it.getSerializable(ARG_PARAM1) as MutableList<ModelQA>?
            productId = it.getInt(ARG_PRODUCTID)
            brand = it.getString(ARG_BRAND)
        }
        viewModel.getFAQAPI(PreferenceHandler.getToken(requireContext())?:"",productId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQABinding.inflate(inflater, container, false)

        postListener()
        getListener()
        setUpRecyclerQA()
        initAppbar()
        return binding.root
    }

    private fun getListener() {
        viewModel.observerGetFAQAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("size", "getListener: " + it.data.toString());
                    binding.progressBar.root.visibility = View.GONE
                    val resposnseqa = it.data
                    if (resposnseqa != null) {
                        try {
                            resposnseqa.queries?.let {
                                param1 = mutableListOf()
                                if (resposnseqa.queries.size > 0) {
                                    hideError()
                                    resposnseqa.queries.forEach { singleModel ->
                                        param1?.add(
                                            ModelQA.Question(
                                                singleModel.question,
                                                singleModel.postedAt,
                                                singleModel.postedBy,
                                                singleModel.isDeletable,
                                                if (singleModel.answer.isEmpty() && singleModel.isDeletable) true else false,
                                                singleModel.questionId
                                            )
                                        )
                                        if (singleModel.answer.isNotEmpty()) {
                                            param1?.add(
                                                ModelQA.Answer(
                                                    singleModel.answer,
                                                    singleModel.repliedAt,
                                                    brand ?: ""
                                                )
                                            )
                                        }
                                        param1?.add(ModelQA.Divider())
                                    }
                                    Log.d("size", "getListener: " + param1!!.size);
                                    myqaAdapter.items = param1!!
                                } else {
                                    //empty queries
                                    showEmpty("Empty Questions", "Post your quesries.")
                                }
                            } ?: run {
                                showError("Error!", resposnseqa.message)
                                Toast.makeText(
                                    requireContext(),
                                    resposnseqa.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            showEmpty("Error!", e.message.toString())
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        }
                        binding.textQuestionTopic.text =
                            String.format(
                                requireContext().getString(com.ayata.clad.R.string.questions_about_this_product_5),
                                param1?.filterIsInstance(ModelQA.Question::class.java)?.size ?: "0",
                                1
                            )
                    }

                }
                Status.LOADING -> {
                    binding.progressBar.root.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    binding.progressBar.root.visibility = View.GONE
                    showEmpty("Error!", it.message.toString())
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun postListener() {
        binding.imageView2.setOnClickListener {
            //check if edit or add
            val jsonObject = JsonObject()
            jsonObject.addProperty("question", binding.editText2!!.text.toString())
            jsonObject.addProperty("product_id", productId)
            getQuestionForEdit()?.let { question ->
                jsonObject.addProperty("q_id", question.questionId ?: -1)
            }
            viewModel.addFAQAPI(PreferenceHandler.getToken(requireContext()) ?: "", jsonObject)

        }
        myqaAdapter.setEditClickListener { question, i ->
            binding.editText2.setText(question.question)
            setQuestionForEdit(question,i)
        }
        viewModel.observerAddFAQAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dismiss()
                    val resposnseqa = it.data
                    if (resposnseqa != null) {
                        try {
                            resposnseqa.queries?.let {
                                //clear edit text
                                binding.editText2.setText("")
                                //reload recyclerview with added params
                                //get first data from list
                                val singleModel = resposnseqa.queries.get(0)
                                param1?.add(
                                    ModelQA.Question(
                                        singleModel.question,
                                        singleModel.postedAt,
                                        singleModel.postedBy,
                                        singleModel.isDeletable,
                                        if (singleModel.answer.isEmpty() && singleModel.isDeletable) true else false,
                                        singleModel.questionId
                                    )
                                )
                                if (singleModel.answer.isNotEmpty()) {
                                    param1?.add(
                                        ModelQA.Answer(
                                            singleModel.answer,
                                            singleModel.repliedAt,
                                            brand ?: ""
                                        )
                                    )
                                }
                                param1?.add(ModelQA.Divider())
                                //
                                if(editPosition!=-1){
                                    myqaAdapter.notifyItemChanged(editPosition)
                                }else{
                                    myqaAdapter.notifyItemInserted(param1!!.size)
                                }


                            } ?: run {
                                Toast.makeText(
                                    requireContext(),
                                    resposnseqa.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                    resetEditValues()
                }
                Status.LOADING -> {
                    progressDialog = ProgressDialog.newInstance("", "")
                    progressDialog.show(parentFragmentManager, "add_progress")
                }
                Status.ERROR -> {
                    resetEditValues()
                    //Handle Error
                    progressDialog.dismiss()
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun setQuestionForEdit(question: ModelQA.Question?,i:Int) {
        isFromEdit = question
        editPosition=i
    }
    private fun resetEditValues() {
        isFromEdit = null
        editPosition=-1
    }

    private fun getQuestionForEdit() = isFromEdit
    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            FAQViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[FAQViewModel::class.java]
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Q&A",
            textDescription = ""
        )
    }


    private fun setUpRecyclerQA() {
        binding.recyclerQa.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myqaAdapter
        }

        param1?.let {
            if (param1!!.size > 0) {
                myqaAdapter.items = param1!!
            } else {
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentQA.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(productId: Int, brand: String) =
            FragmentQA().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PRODUCTID, productId)
                    putString(ARG_BRAND, brand)
                }
            }
    }

    private fun showEmpty(title: String, it: String) {
        binding.recyclerQa.visibility = View.GONE
        Caller().empty(title, it, requireContext(), binding.root)
    }

    private fun showError(title: String, it: String) {
        binding.recyclerQa.visibility = View.GONE
        Caller().error(title, it, requireContext(), binding.root)
    }

    private fun hideError() {
        binding.recyclerQa.visibility = View.VISIBLE
        Caller().hideErrorEmpty(binding.root)
    }
}