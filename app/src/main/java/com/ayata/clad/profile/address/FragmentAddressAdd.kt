package com.ayata.clad.profile.address

import android.os.Bundle
import android.util.Log
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
import com.ayata.clad.profile.address.response.Detail
import com.ayata.clad.profile.address.viewmodel.AddressViewModel
import com.ayata.clad.profile.address.viewmodel.AddressViewModelFactory
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.PreferenceHandler
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject

class FragmentAddressAdd : Fragment() {

    private lateinit var binding: FragmentAddressAddBinding
    private lateinit var viewModel: AddressViewModel
    var isShipApi = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddressAddBinding.inflate(inflater, container, false)
        setUpViewModel()
        getMyBundle()
        initAppbar()

        binding.btnSave.setOnClickListener {
            validateTextField(binding.textInputTitle)
            validateTextField(binding.textInputAddress)
            validateTextField(binding.textInputCity)
            validateTextField(binding.textInputPhone)
            validateTextField(binding.textInputState)
            validateTextField(binding.textInputZip)
            //save api
            if (isShipApi){
                saveAddressShippingApi()}
            else
            { saveAddressUserApi()}
        }
        return binding.root
    }

    private fun getMyBundle() {
        Log.d("testbundle", "getMyBundle: ");
        if (arguments != null) {
            val ship = requireArguments().getBoolean("ship", false)
            val data = requireArguments().getSerializable("data")?.let { it as Detail }
            //set previous data
            Log.d("testbundle", "getMyBundle: " + data);
            if (data != null) {
                setMyData(data)
            }
            isShipApi = ship
            Log.d("tetst", "getMyBundle: " + isShipApi);
        }else{
            Log.d("testbundle", "getMyBundle: null");
        }
    }

    private fun setMyData(data: Detail) {
        binding.textInputTitle.editText!!.setText(data.title)
        binding.textInputAddress.editText!!.setText(data.streetName)
        binding.textInputCity.editText!!.setText(data.city)
        binding.textInputState.editText!!.setText(data.state.toString())
        binding.textInputZip.editText!!.setText(data.zipCode)
        binding.textInputPhone.editText!!.setText(data.contactNumber)
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            AddressViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[AddressViewModel::class.java]
        viewModel.observeShippingAddAddress().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.spinKit.visibility = View.GONE
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            requireActivity().supportFragmentManager.popBackStackImmediate()
                        } catch (e: Exception) {
                        }
                    }

                }
                Status.LOADING -> {
                    binding.spinKit.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    binding.spinKit.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
        viewModel.observeUserAddAddress().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.spinKit.visibility = View.GONE
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            requireActivity().supportFragmentManager.popBackStackImmediate()
                        } catch (e: Exception) {
                        }
                    }

                }
                Status.LOADING -> {
                    binding.spinKit.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    binding.spinKit.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun saveAddressShippingApi() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("title", binding.textInputTitle.editText!!.text.toString())
        jsonObject.addProperty("street_name", binding.textInputAddress.editText!!.text.toString())
        jsonObject.addProperty("city", binding.textInputCity.editText!!.text.toString())
        jsonObject.addProperty("state", binding.textInputState.editText!!.text.toString())
        jsonObject.addProperty("postal_code", binding.textInputZip.editText!!.text.toString())
        jsonObject.addProperty("contact_number", binding.textInputPhone.editText!!.text.toString())
        viewModel.addShippingAddress(
            Constants.Bearer + " " + PreferenceHandler.getToken(
                requireContext()
            )!!, jsonObject
        )

    }

    private fun saveAddressUserApi() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("title", binding.textInputTitle.editText!!.text.toString())
        jsonObject.addProperty("street_name", binding.textInputAddress.editText!!.text.toString())
        jsonObject.addProperty("city", binding.textInputCity.editText!!.text.toString())
        jsonObject.addProperty("state", binding.textInputState.editText!!.text.toString())
        jsonObject.addProperty("postal_code", binding.textInputZip.editText!!.text.toString())
        jsonObject.addProperty("contact_number", binding.textInputPhone.editText!!.text.toString())
        viewModel.addUserAddress(
            Constants.Bearer + " " + PreferenceHandler.getToken(requireContext())!!,
            jsonObject
        )

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