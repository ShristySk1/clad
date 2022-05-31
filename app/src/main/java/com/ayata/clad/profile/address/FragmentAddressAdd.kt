package com.ayata.clad.profile.address

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentAddressAddBinding
import com.ayata.clad.profile.address.response.Detail
import com.ayata.clad.profile.address.viewmodel.AddressViewModel
import com.ayata.clad.profile.address.viewmodel.AddressViewModelFactory
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.ProgressDialog
import com.ayata.clad.utils.removeDoubleQuote
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject

class FragmentAddressAdd : Fragment() {

    private lateinit var binding: FragmentAddressAddBinding
    private lateinit var viewModel: AddressViewModel
    var isShipApi = false
    var spin_state = "Bagmati Province"
    var spin_district = "Kathmandu"
    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddressAddBinding.inflate(inflater, container, false)
        setUpViewModel()
        getMyBundle()
        initAppbar()
        //setspinner
        setSpinner()

        binding.btnSave.setOnClickListener {
           val p1= validateTextField(binding.textInputTitle)
           val p2= validateTextField(binding.textInputAddress)
           val p3= validateTextField(binding.textInputCity)
           val p4= validateTextFieldPhone(binding.textInputPhone)
//            validateTextField(binding.textInputState)
            val p5=validateTextField(binding.textInputZip)

            if(!p1 or !p2 or !p3 or !p4 or !p5){
                return@setOnClickListener
            }
            //save api
            if (isShipApi) {
                saveAddressShippingApi()
            } else {
                saveAddressUserApi()
            }
        }
        return binding.root
    }

    private fun setSpinner() {
        val district = arrayListOf<String>("Kathmandu")
        val list2 =
            arrayListOf<ModelTest>(ModelTest(district, "Bagmati Province"))

        if (binding.spinner != null) {
            val adapter =
                CustomArrayAdapter(requireContext(), R.layout.spinner_custom, R.id.text_view, list2)
            binding.spinner.adapter = adapter
            binding.spinner.setPrompt("Select State");
            binding.spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    spin_state=list2[position].state
                    setSpinnerDistrict(list2[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

    }

    private fun setSpinnerDistrict(modelTest: ModelTest) {
        val adapter = CustomArrayAdapterString(
            requireContext(),
            R.layout.spinner_custom,
            R.id.text_view,
            modelTest.district
        )
        binding.spinnerDistrict.adapter = adapter
        binding.spinnerDistrict.setPrompt("Select District");

        binding.spinnerDistrict.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                spin_district=modelTest.district[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
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
        } else {
            Log.d("testbundle", "getMyBundle: null");
        }
    }

    private fun setMyData(data: Detail) {
        binding.textInputTitle.editText!!.setText(data.title)
        binding.textInputAddress.editText!!.setText(data.streetName)
        binding.textInputCity.editText!!.setText(data.city)
//        binding.textInputState.editText!!.setText(data.state.toString())
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
                    progressDialog.dismiss()
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            Toast.makeText(requireContext(),jsonObject.get("message").toString().removeDoubleQuote(),Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.popBackStackImmediate()
                        } catch (e: Exception) {
                        }
                    }
                }
                Status.LOADING -> {
                    progressDialog = ProgressDialog.newInstance("", "")
                    progressDialog.show(parentFragmentManager, "ship_progress")
                }
                Status.ERROR -> {
                    //Handle Error
                    progressDialog.dismiss()
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
        viewModel.observeUserAddAddress().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                   progressDialog.dismiss()
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            Toast.makeText(requireContext(),jsonObject.get("message").toString().removeDoubleQuote(),Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.popBackStackImmediate()
                        } catch (e: Exception) {
                        }
                    }

                }
                Status.LOADING -> {
                    progressDialog = ProgressDialog.newInstance("", "")
                    progressDialog.show(parentFragmentManager, "user_progress")
                }
                Status.ERROR -> {
                    //Handle Error
                    progressDialog.dismiss()
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
        jsonObject.addProperty("state", 3)
        jsonObject.addProperty("district", spin_district)
        jsonObject.addProperty("postal_code", binding.textInputZip.editText!!.text.toString())
        jsonObject.addProperty("phone_no", binding.textInputPhone.editText!!.text.toString())
        viewModel.addShippingAddress(
            PreferenceHandler.getToken(
                requireContext()
            )!!, jsonObject
        )

    }

    private fun saveAddressUserApi() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("title", binding.textInputTitle.editText!!.text.toString())
        jsonObject.addProperty("street_name", binding.textInputAddress.editText!!.text.toString())
        jsonObject.addProperty("city", binding.textInputCity.editText!!.text.toString())
        jsonObject.addProperty("state", 3)
        jsonObject.addProperty("district", spin_district)
        jsonObject.addProperty("postal_code", binding.textInputZip.editText!!.text.toString())
        jsonObject.addProperty("phone_no", binding.textInputPhone.editText!!.text.toString())
        viewModel.addUserAddress(
            PreferenceHandler.getToken(requireContext())!!,
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
    private fun validateTextFieldPhone(textField: TextInputLayout): Boolean {
        val data = textField.editText!!.text.toString().trim()
        return if (data.isEmpty() or (data == " ")) {
            textField.error = "This field can't be empty"
            false
        }else if(data.length!=10){
            textField.error = "This field must have 10 digit number."
           false
        } else {
            textField.error = null
            true
        }
    }

}