package com.ayata.clad.shopping_bag.shipping

import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.core.text.underline
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentShippingBinding


class FragmentShipping : Fragment() {

    private lateinit var binding:FragmentShippingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentShippingBinding.inflate(inflater, container, false)

        binding.textTerms.text=SpannableStringBuilder().append("By placing an order you agree to our \n")
            .underline {color(ContextCompat.getColor(requireContext(),R.color.black)) { append("Terms and Conditions.") }}

        return binding.root
    }

}