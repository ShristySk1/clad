package com.ayata.clad.profile.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayata.clad.MainActivity
import com.ayata.clad.databinding.FragmentProfileEditBinding


class FragmentProfileEdit : Fragment() {

    private lateinit var binding:FragmentProfileEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentProfileEditBinding.inflate(inflater, container, false)
        initAppbar()

        binding.btnSave.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }
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

//    private fun validateFloat(data_type: TextInputLayout): Boolean? {
//        val data_types = data_type.editText!!.text.toString().trim { it <= ' ' }
//        return if (data_types.isEmpty() or (data_types == " ")) {
//            data_type.error = getString(R.string.empty_field1)
//            false
//        } else if (!validateString(data_types)) {
//            data_type.error = null
//            true
//        } else {
//            data_type.error = getString(R.string.empty_field2)
//            false
//        }
//    }

//    private fun validateEmail(): Boolean {
//        email = textLayoutEmail.getEditText().getText().toString().trim { it <= ' ' }
//        return if (email.isEmpty()) {
////            textLayoutEmail.setError("Field can't be empty");
//            textLayoutEmail.setError("")
//            textLayoutEmail.setErrorEnabled(false)
//            true
//        } else if (!email.contains("@")) {
//            textLayoutEmail.setErrorEnabled(true)
//            textLayoutEmail.setError("Fill Valid Email")
//            false
//        } else if (!email.contains(".")) {
//            textLayoutEmail.setErrorEnabled(true)
//            textLayoutEmail.setError("Fill Valid Email")
//            false
//        } else if (email.contains(" ")) {
//            textLayoutEmail.setErrorEnabled(true)
//            textLayoutEmail.setError("Email cannot contain blank space.")
//            false
//        } else {
//            textLayoutEmail.setError("")
//            textLayoutEmail.setErrorEnabled(false)
//            true
//        }
//    }
}