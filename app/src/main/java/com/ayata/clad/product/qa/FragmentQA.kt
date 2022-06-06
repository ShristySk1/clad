package com.ayata.clad.product.qa

import android.os.Bundle
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
import com.ayata.clad.utils.ProgressDialog
import com.ayata.clad.utils.removeDoubleQuote
import com.google.gson.JsonObject
import java.io.Serializable


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PRODUCTID = "param3"


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentQA.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentQA : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: MutableList<ModelQA>? = null
    private var param2: Int? = null
    private var productId: Int? = null
    private lateinit var binding: FragmentQABinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var viewModel: FAQViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as MutableList<ModelQA>?
            param2 = it.getInt(ARG_PARAM2)
            productId=it.getInt(ARG_PRODUCTID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQABinding.inflate(inflater, container, false)
        setUpRecyclerQA()
        postListener()
        initAppbar()
        return binding.root
    }

    private fun postListener() {

        binding.imageView2.setOnClickListener {
//            val jsonObject = JsonObject()
//            jsonObject.addProperty("question", binding.editText2!!.text.toString())
//            jsonObject.addProperty("product_id", productId)
//            viewModel.addFAQAPI(jsonObject)
        }
        viewModel.observerAddFAQAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dismiss()
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            Toast.makeText(
                                requireContext(),
                                jsonObject.get("message").toString().removeDoubleQuote(),
                                Toast.LENGTH_SHORT
                            ).show()
                            //clear edit text
                            binding.editText2.setText("")
                            //reload recyclerview with added params
//                            param1.add(M)

                        } catch (e: Exception) {
                        }
                    }

                }
                Status.LOADING -> {
                    progressDialog = ProgressDialog.newInstance("", "")
                    progressDialog.show(parentFragmentManager, "add_progress")
                }
                Status.ERROR -> {
                    //Handle Error
                    progressDialog.dismiss()
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

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
        binding.textQuestionTopic.text =
            String.format(
                requireContext().getString(com.ayata.clad.R.string.questions_about_this_product_5),
                param2,
                1
            )
        param1?.let {
            if (param1!!.size > 0) {
                binding.recyclerQa.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = AdapterQaMultiple(param1!!)

                }
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
        fun newInstance(param1: List<ModelQA>, param2: Int,productId:Int) =
            FragmentQA().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1 as Serializable)
                    putInt(ARG_PARAM2, param2)
                    putInt(ARG_PRODUCTID, productId)
                }
            }
    }
}