package com.ayata.clad.profile.reviews

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentMyReviewsBinding
import com.ayata.clad.product.adapter.AdapterMyReviews
import com.ayata.clad.profile.reviews.model.Review
import com.ayata.clad.utils.MyLayoutInflater

class FragmentMyReviews : Fragment() {
    lateinit var binding: FragmentMyReviewsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentMyReviewsBinding.inflate(inflater, container, false)
        initAppbar()
        initRecyclerView()
        return binding.root
    }


    private fun initRecyclerView() {
        val data = arguments?.getSerializable("datas") as? List<Review>
        val isFetched = arguments?.getBoolean("isFetched") as Boolean
        Log.d("testdatas", "initRecyclerView: " + data+isFetched);
        hideEmpty()
        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AdapterMyReviews(context, data ?: arrayListOf()).also {
                it.setReviewClickListener {
                    (activity as MainActivity).openFragmentReviewForm(it)
                }
            }
        }
        if (data?.size == 0) {
            if (FragmentMyReviewsList.isApiFetched) {
                showEmpty("No products to review")
            }
        } else {
            if (FragmentMyReviewsList.isApiFetched) {
                hideEmpty()
            }

        }
    }

    private fun initAppbar() {
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "My Reviews",
            textDescription = ""
        )
        (activity as MainActivity).showBottomNavigation(false)
    }

    private fun showEmpty(it: String) {
        MyLayoutInflater().onAddField(
            requireContext(),
            binding.root,
            R.layout.layout_error,
            R.drawable.ic_cart,
            "Empty!",
            it
        )
    }

    private fun hideEmpty() {
        if (binding.root.findViewById<LinearLayout>(R.id.layout_root) != null) {
            MyLayoutInflater().onDelete(
                binding.root,
                binding.root.findViewById(R.id.layout_root)
            )
        }
    }

}