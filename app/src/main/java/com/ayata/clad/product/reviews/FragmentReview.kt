package com.ayata.clad.product.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentReviewBinding
import com.ayata.clad.home.response.Reviews
import com.ayata.clad.product.reviews.adapter.AdapterReview
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.MyLayoutInflater
import java.io.Serializable

private const val ARG_PARAM1 = "param1"

class FragmentReview : Fragment() {
    private lateinit var binding: FragmentReviewBinding
    private lateinit var param1: Reviews
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as Reviews
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReviewBinding.inflate(inflater, container, false)
        initView()
        inirRecyclerView()
        return binding.root
    }

    private fun initView() {
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "",
            textDescription = ""
        )
        (activity as MainActivity).showBottomNavigation(true)

        //set total rating
        binding.tvTotalReview.text = "${param1.totalReview} REVIEWS"
        binding.ratingBar1.rating = param1.rating.toFloat()
        binding.tvRating.text = "${param1.rating}/5"
    }

    private fun inirRecyclerView() {
        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = param1?.let { it?.review_details?.let { it1 -> AdapterReview(context, it1) } }
        }
        param1?.let {
            it?.review_details?.let {
                if (it.size > 0) {
                    hideError()

                } else {
                    showError("Empty Reviews","No Reviews has been added yet.")
                }

            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Reviews) =
            FragmentReview().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1 as Serializable)
                }
            }
    }

    private fun showError(title:String,it: String) {
        binding.rvReviews.visibility = View.GONE
        MyLayoutInflater().onAddField(
            requireContext(),
            binding.root,
            R.layout.layout_error,
            Constants.ERROR_TEXT_DRAWABLE,
            title,
            it
        )

    }

    private fun hideError() {
        binding.rvReviews.visibility = View.VISIBLE
        if (binding.root.findViewById<LinearLayout>(R.id.layout_root) != null) {
            MyLayoutInflater().onDelete(
                binding.root,
                binding.root.findViewById(R.id.layout_root)
            )
        }
    }
}