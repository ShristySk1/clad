package com.ayata.clad.preorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentPreorderBinding

class FragmentPreorder : Fragment() {

    private lateinit var binding:FragmentPreorderBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentPreorderBinding.inflate(inflater, container, false)

        initAppbar()
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1("Preorder",
            isSearch = true,
            isProfile = true,
            isClose = false,
            isLogo = false
        )
    }
}