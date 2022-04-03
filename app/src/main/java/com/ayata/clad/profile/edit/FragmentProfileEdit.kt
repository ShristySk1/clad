package com.ayata.clad.profile.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentProfileEditBinding
import com.ayata.clad.profile.account.AccountViewModel
import com.ayata.clad.profile.edit.response.Details
import com.ayata.clad.profile.viewmodel.ProfileViewModel
import com.ayata.clad.profile.viewmodel.ProfileViewModelFactory
import com.ayata.clad.utils.PreferenceHandler
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*


class FragmentProfileEdit : Fragment() {
    val TAG = "FragmentProfileEdit"
    private lateinit var binding: FragmentProfileEditBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var accountViewmodel: AccountViewModel

    private var email = ""
    private var gender = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        initAppbar()
        setUpViewModel()
        initView()
        return binding.root
    }

    private fun setUpViewModel() {
        viewModel =
            ViewModelProvider(
                this,
                ProfileViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
            )
                .get(ProfileViewModel::class.java)

        // init ViewModel
        accountViewmodel =
            ViewModelProviders.of(requireActivity()).get(AccountViewModel::class.java)
    }
    private fun setDataToView(detail: Details) {
        binding.textInputName.editText?.setText(detail.fullName)
        binding.textInputEmail.editText?.setText(detail.email)
        binding.textInputDOB.editText?.setText(detail.dob)
        binding.textInputPhone.editText?.setText(detail.phone_no)
        gender=detail.gender?:""
        when (detail.gender) {
            "M" -> {
                binding.radioGroupGender.check(R.id.rb_male)
            }
            "F" -> {
                binding.radioGroupGender.check(R.id.rb_female)
            }
            "O" -> {
                binding.radioGroupGender.check(R.id.rb_other)
            }
        }
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Edit Profile Info",
            textDescription = ""
        )
    }

    private fun initView() {
        setPreviousValue()
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.textInputDOB.editText!!.setText(SimpleDateFormat("yyyy-MM-dd").format(cal.time))
            }
        binding.textEditDob.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        binding.radioGroupGender.setOnCheckedChangeListener(object :
            RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                when (p1) {
                    R.id.rb_male -> {
                        gender = "M"
                    }
                    R.id.rb_female -> {
                        gender = "F"
                    }
                    R.id.rb_other -> {
                        gender = "O"
                    }
                }
            }
        })

        binding.btnSave.setOnClickListener {
            val v_email = validateEmail()
            val v_name = validateTextField(binding.textInputName, "Name")
            val v_dob = validateTextField(binding.textInputDOB, "DOB")
            val v_phone = validateTextField(binding.textInputPhone, "Contact Number")
//            val v_gender = validateRadioButton()
            if (v_email && v_dob && v_dob && v_name && v_phone) {
                saveToServer()
            }


        }
    }

    private fun validateRadioButton(): Boolean {
        return binding.radioGroupGender.getCheckedRadioButtonId() != -1
    }

    private fun saveToServer() {
        val name = binding.textInputName.editText!!.text.toString().trim { it <= ' ' }
        val dob = binding.textInputDOB.editText!!.text.toString().trim { it <= ' ' }
        val phone = binding.textInputPhone.editText!!.text.toString().trim { it <= ' ' }
        val myViewData =
            Details(dob = dob, email, fullName = name, gender = gender, phone_no = phone)
        viewModel.profileDetailUpdateAPI(PreferenceHandler.getToken(context)!!, myViewData)
        viewModel.postProfileAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "saveToServer: ");
                    binding.spinKit.visibility = View.GONE
                    val jsonObject = it.data
                    if (jsonObject != null) {

                        PreferenceHandler.setDOB(requireContext(), dob)
                        PreferenceHandler.setUsername(requireContext(), name)
                        PreferenceHandler.setGender(requireContext(), gender)
                        PreferenceHandler.setPhone(requireContext(), phone)
                        Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(context, "null", Toast.LENGTH_SHORT).show()
                    }

                }
                Status.LOADING -> {
                    binding.spinKit.visibility = View.VISIBLE
                    Log.d(TAG, "saveToServer: load");

                }
                Status.ERROR -> {
                    //Handle Error
                    binding.spinKit.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()


                }
            }
        })


    }

    private fun validateTextField(textField: TextInputLayout, dataTitle: String): Boolean {
        val data = textField.editText!!.text.toString().trim { it <= ' ' }
        return if (data.isEmpty() or (data == " ")) {
            textField.error = "$dataTitle field can't be empty"
            false
        } else if (dataTitle.equals("Contact Number") && data.length < 10) {
            textField.error = "Invalid $dataTitle"
            false
        } else {
            textField.error = null
            true
        }
    }

    private fun validateEmail(): Boolean {
        val textLayoutEmail = binding.textInputEmail
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

    private fun setPreviousValue() {
//        if (arguments != null) {
//            arguments?.let {
//                val detail=it.getSerializable(Constants.EDIT_PROFILE) as Details
//                setDataToView(detail = detail)
//            }
//
//        }
//        binding.spinKit.visibility = View.VISIBLE
//        accountViewmodel.getAccountDetails().observe(viewLifecycleOwner, {
//            setDataToView(it)
//            binding.spinKit.visibility = View.GONE
//        })
        val detail: Details = Details(
            PreferenceHandler.getDOB(requireContext()),
            PreferenceHandler.getEmail(requireContext()) ?: "",
            PreferenceHandler.getUsername(requireContext()) ?: "",
            PreferenceHandler.getGender(requireContext()),
            PreferenceHandler.getPhone(requireContext())
        )
        setDataToView(detail)

    }
}