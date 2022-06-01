package com.ayata.clad.product.qa

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.databinding.FragmentQABinding
import com.ayata.clad.product.ModelColor
import com.ayata.clad.product.adapter.AdapterQA
import java.text.MessageFormat


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentQA.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentQA : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private lateinit var binding: FragmentQABinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQABinding.inflate(inflater, container, false)
        setUpRecyclerQA()
        initAppbar()
        return binding.root
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
            String.format(requireContext().getString(com.ayata.clad.R.string.questions_about_this_product_5),"14",1)
        val myQAList = listOf("s", "a", "b")
        if (myQAList.size > 0) {
            binding.recyclerQa.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = AdapterQA(myQAList, 1, object : AdapterQA.OnItemClickListener {
                    override fun onColorClicked(color: ModelColor, position: Int) {

                    }

                })
            }
        } else {
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
        fun newInstance(param1: String, param2: String) =
            FragmentQA().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}