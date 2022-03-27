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
import com.ayata.clad.databinding.FragmentMyReviewFormBinding


class FragmentMyReviewsForm : Fragment() {
    lateinit var binding: FragmentMyReviewFormBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentMyReviewFormBinding.inflate(inflater, container, false)
        initAppbar()
        return binding.root
    }
    private fun initAppbar() {
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Review",
            textDescription = ""
        )
        (activity as MainActivity).showBottomNavigation(false)
    }

}