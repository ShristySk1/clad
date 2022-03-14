package com.ayata.clad.profile.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ayata.clad.MainActivity
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentAddressAddBinding
import com.ayata.clad.profile.address.viewmodel.AddressViewModel
import com.ayata.clad.profile.address.viewmodel.AddressViewModelFactory
import com.ayata.clad.utils.PreferenceHandler
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject

class FragmentAddressAdd : Fragment() {

    private lateinit var binding: FragmentAddressAddBinding
    private lateinit var viewModel: AddressViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddressAddBinding.inflate(inflater, container, false)
        setUpViewModel()
        initAppbar()

        binding.btnSave.setOnClickListener {
            validateTextField(binding.textInputTitle)
            validateTextField(binding.textInputAddress)
            validateTextField(binding.textInputCity)
            validateTextField(binding.textInputLine)
            validateTextField(binding.textInputPhone)
            validateTextField(binding.textInputState)
            validateTextField(binding.textInputZip)
            //save api
            saveAddressApi()
        }
        return binding.root
    }
    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            AddressViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[AddressViewModel::class.java]
        viewModel.observeAddAddress().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.spinKit.visibility=View.GONE
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            requireActivity().supportFragmentManager.popBackStackImmediate()
                        } catch (e: Exception) {
                        }
                    }

                }
                Status.LOADING -> {
                    binding.spinKit.visibility=View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    binding.spinKit.visibility=View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun saveAddressApi() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("title", binding.textInputTitle.editText!!.text.toString())
        jsonObject.addProperty("street_name", binding.textInputAddress.editText!!.text.toString())
        jsonObject.addProperty("city", binding.textInputCity.editText!!.text.toString())
        jsonObject.addProperty("state", binding.textInputState.editText!!.text.toString())
        jsonObject.addProperty("postal_code", binding.textInputZip.editText!!.text.toString())
        jsonObject.addProperty("contact_number", binding.textInputPhone.editText!!.text.toString())
        viewModel.addAddress(PreferenceHandler.getToken(requireContext())!!, jsonObject)

    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Add New Address",
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