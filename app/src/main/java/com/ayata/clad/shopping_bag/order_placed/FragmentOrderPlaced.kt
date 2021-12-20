package com.ayata.clad.shopping_bag.order_placed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentOrderPlacedBinding


class FragmentOrderPlaced : Fragment() {

    private lateinit var binding:FragmentOrderPlacedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentOrderPlacedBinding.inflate(inflater, container, false)

//        initView()
        return binding.root
    }

    private fun initView(){

        binding.textDate.text="June 25 2022"

        binding.btnView.setOnClickListener {
            Toast.makeText(context,"View Details",Toast.LENGTH_SHORT).show()
        }
    }


}