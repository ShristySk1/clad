package com.ayata.clad.shopping_bag.order_placed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentOrderPlacedBinding
import com.ayata.clad.order.FragmentOrderDetail


class FragmentOrderPlaced : Fragment() {

    private lateinit var binding:FragmentOrderPlacedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentOrderPlacedBinding.inflate(inflater, container, false)

        initAppbar()
        initView()
        return binding.root
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(false)
    }
    private fun initView(){

        binding.textDate.text="June 25 2022"

        binding.btnView.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment,FragmentOrderDetail())
                .addToBackStack(null).commit()
        }
    }


}