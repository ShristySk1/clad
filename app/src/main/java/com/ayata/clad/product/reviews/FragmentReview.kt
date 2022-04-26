package com.ayata.clad.product.reviews

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentReviewBinding
import com.ayata.clad.home.response.Reviews
import com.ayata.clad.product.reviews.adapter.AdapterReview
import com.ayata.clad.productlist.ItemOffsetDecoration
import com.ayata.clad.profile.reviews.adapter.AdapterImageViewType
import com.ayata.clad.profile.reviews.imageswipe.FragmentImageSwiper
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.MyLayoutInflater
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import java.io.Serializable

private const val ARG_PARAM1 = "param1"

class FragmentReview : Fragment() {
    private lateinit var binding: FragmentReviewBinding
    private lateinit var param1: Reviews
    private lateinit var myAdapter:AdapterReview
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
        (activity as MainActivity).showBottomNavigation(false)

        //set total rating
        binding.tvTotalReview.text = "${param1.totalReview} REVIEWS"
        binding.ratingBar1.rating = param1.rating!!.toFloat()
        binding.tvRating.text = "${param1.rating}/5"
    }

    private fun inirRecyclerView() {
        myAdapter=param1?.let {
            it.review_details.let { it1 ->
                AdapterReview(context, it1?: listOf())
            }
        }
        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = myAdapter
        }
        myAdapter.setReviewClickListener { image, i ->
            Log.d("testimage", "inirRecyclerView: "+image.size);
            val frag=FragmentImageSwiper.newInstance(image.map { it.image },i)
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment,frag).addToBackStack(null).commit()
        }
                if (myAdapter.itemCount > 0) {
                    hideError()

                } else {
                    showError("Empty Reviews", "No Reviews has been added yet.")
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

    private fun showError(title: String, it: String) {
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