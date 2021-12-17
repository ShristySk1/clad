package com.ayata.clad.product

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentProductDetail2Binding
import com.ayata.clad.databinding.FragmentProductDetailBinding


class FragmentProductDetail2 : Fragment() {
    private lateinit var binding: FragmentProductDetail2Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductDetail2Binding.inflate(inflater, container, false)
        (activity as MainActivity).hideToolbar()
        (activity as MainActivity).showBottomNav(false)
        return binding.root
        }
    }

