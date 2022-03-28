package com.ayata.clad.profile.reviews

import android.os.Bundle
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentMyReviewsBinding
import com.ayata.clad.databinding.FragmentMyorderBinding
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.product.adapter.AdapterMyReviews
import com.ayata.clad.profile.reviews.model.ModelReview

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
        val data = arguments?.getSerializable("datas") as? List<ModelReview>
        binding.rvReviews.apply {
            layoutManager=LinearLayoutManager(context)
            adapter=AdapterMyReviews(context, data?.let { it }?: kotlin.run { arrayListOf() }).also {
                it.setReviewClickListener {
                    (activity as MainActivity).openFragmentReviewForm(it)
                }
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

}