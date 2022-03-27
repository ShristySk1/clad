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
        binding.rvReviews.apply {
            layoutManager=LinearLayoutManager(context)
            adapter=AdapterMyReviews(context, listOf(ModelReview("a"),ModelReview("b"))).also {
                it.setReviewClickListener {
                    parentFragmentManager.beginTransaction().replace(R.id.main_fragment,FragmentMyReviewsForm()).addToBackStack(null).commit()
                }
            }
        }
        return binding.root
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