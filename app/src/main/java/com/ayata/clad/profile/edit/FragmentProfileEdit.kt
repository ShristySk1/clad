package com.ayata.clad.profile.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.ayata.clad.MainActivity
import com.ayata.clad.databinding.FragmentProfileEditBinding
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*


class FragmentProfileEdit : Fragment() {

    private lateinit var binding:FragmentProfileEditBinding

    private var email=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentProfileEditBinding.inflate(inflater, container, false)
        initAppbar()
        initView()
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Edit Profile Info",
            textDescription = ""
        )
    }

    private fun initView(){
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.textInputDOB.editText!!.setText(SimpleDateFormat("yyyy-MM-dd").format(cal.time))
            }
        binding.textEditDob.setOnClickListener {
            DatePickerDialog(requireContext(),
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnSave.setOnClickListener {
            validateEmail()
            validateTextField(binding.textInputName,"Name")
            validateTextField(binding.textInputDOB,"DOB")
//            requireActivity().supportFragmentManager.popBackStackImmediate()
        }
    }

    private fun validateTextField(textField: TextInputLayout,dataTitle:String): Boolean? {
        val data = textField.editText!!.text.toString().trim { it <= ' ' }
        return if (data.isEmpty() or (data == " ")) {
            textField.error = "$dataTitle field can't be empty"
            false
        } else{
            textField.error = null
            true
        }
    }

    private fun validateEmail(): Boolean {
        val textLayoutEmail=binding.textInputEmail
        email = textLayoutEmail.editText!!.text.toString().trim { it <= ' ' }
        return if (email.isEmpty()) {
            textLayoutEmail.error = "Email field can't be empty"
            textLayoutEmail.isErrorEnabled = true
            true
        } else if (!email.contains("@")) {
            textLayoutEmail.isErrorEnabled = true
            textLayoutEmail.error = "Fill Valid Email"
            false
        } else if (!email.contains(".")) {
            textLayoutEmail.isErrorEnabled = true
            textLayoutEmail.error = "Fill Valid Email"
            false
        } else if (email.contains(" ")) {
            textLayoutEmail.isErrorEnabled = true
            textLayoutEmail.error = "Email cannot contain blank space."
            false
        } else {
            textLayoutEmail.error = ""
            textLayoutEmail.isErrorEnabled = false
            true
        }
    }
}