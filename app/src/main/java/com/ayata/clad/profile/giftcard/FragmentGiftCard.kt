package com.ayata.clad.profile.giftcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.databinding.FragmentAccountBinding
import com.ayata.clad.databinding.FragmentGiftcardBinding

class FragmentGiftCard : Fragment() {
    lateinit var binding: FragmentGiftcardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentGiftcardBinding.inflate(inflater, container, false)

        return binding.root
    }


}