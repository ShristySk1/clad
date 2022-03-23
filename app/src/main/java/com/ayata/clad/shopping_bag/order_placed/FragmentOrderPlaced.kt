package com.ayata.clad.shopping_bag.order_placed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ayata.clad.MainActivity
import com.ayata.clad.databinding.FragmentOrderPlacedBinding


class FragmentOrderPlaced : Fragment() {

    private lateinit var binding: FragmentOrderPlacedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderPlacedBinding.inflate(inflater, container, false)
        initAppbar()
        initView()
        return binding.root
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(false)
    }

    private fun initView() {
        binding.textDate.text = ""
        binding.btnView.text = "Shop More"
        binding.btnView.setOnClickListener {
//            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            (activity as MainActivity).openHomePage()

        }
    }


}