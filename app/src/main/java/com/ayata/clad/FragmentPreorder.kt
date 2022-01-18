package com.ayata.clad

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ayata.clad.databinding.FragmentPreorderBinding

class FragmentPreorder : Fragment() {

    private lateinit var binding:FragmentPreorderBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentPreorderBinding.inflate(inflater, container, false)

        return binding.root
    }

}