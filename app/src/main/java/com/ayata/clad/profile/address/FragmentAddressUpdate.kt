package com.ayata.clad.profile.address

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentAddressAddBinding
import com.google.android.material.textfield.TextInputLayout

class FragmentAddressUpdate : Fragment() {

    private lateinit var binding:FragmentAddressAddBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAddressAddBinding.inflate(inflater, container, false)

        initAppbar()

        binding.btnSave.setOnClickListener {
            validateTextField(binding.textInputTitle)
            validateTextField(binding.textInputAddress)
            validateTextField(binding.textInputCity)
            validateTextField(binding.textInputLine)
            validateTextField(binding.textInputPhone)
            validateTextField(binding.textInputState)
            validateTextField(binding.textInputZip)
//            requireActivity().supportFragmentManager.popBackStackImmediate()
        }
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Update Address",
            textDescription = ""
        )
    }

    private fun validateTextField(textField: TextInputLayout): Boolean {
        val data = textField.editText!!.text.toString().trim()
        return if (data.isEmpty() or (data == " ")) {
            textField.error = "This field can't be empty"
            false
        } else {
            textField.error = null
            true
        }
    }

}