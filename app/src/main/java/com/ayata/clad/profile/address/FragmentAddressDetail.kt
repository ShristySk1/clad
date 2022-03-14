package com.ayata.clad.profile.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentAddressDetailBinding
import com.ayata.clad.profile.address.adapter.AdapterAddress
import com.ayata.clad.profile.address.response.Detail
import com.ayata.clad.profile.address.viewmodel.AddressViewModel
import com.ayata.clad.profile.address.viewmodel.AddressViewModelFactory
import com.ayata.clad.shopping_bag.model.ModelShippingAddress
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.PreferenceHandler

class FragmentAddressDetail : Fragment(), AdapterAddress.OnItemClickListener {

    private lateinit var binding: FragmentAddressDetailBinding
    private lateinit var adapterAddress: AdapterAddress
    private var listAddress = ArrayList<ModelShippingAddress>()
    private lateinit var viewModel: AddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddressDetailBinding.inflate(inflater, container, false)
        initAppbar()
        initRecycler()
        setUpViewModel()
        viewModel.getAddress(Constants.Bearer + " " + PreferenceHandler.getToken(requireContext()))
        binding.llAddAddress.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, FragmentAddressAdd()).addToBackStack(null).commit()
        }

        return binding.root
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            AddressViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[AddressViewModel::class.java]
        viewModel.observeAddress().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.spinKit.visibility = View.GONE
                    try {
                        it.data?.details?.let { it1 -> prepareListAddress(it1) }
                    } catch (e: Exception) {
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

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Address Detail",
            textDescription = ""
        )
    }

    private fun initRecycler() {

        adapterAddress = AdapterAddress(context, listAddress, this)
        binding.recyclerView.apply {
            adapter = adapterAddress
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        if (listAddress.size >= 2) {
            binding.addNewBtn.visibility = View.GONE
            binding.llAddAddress.visibility = View.GONE
        } else {
            binding.addNewBtn.visibility = View.GONE
            binding.llAddAddress.visibility = View.VISIBLE
        }
    }

    private fun prepareListAddress(details: List<Detail>) {

        listAddress.clear()
        for (detail in details) {
            listAddress.add(
                ModelShippingAddress(
                    detail.title,
                    "${detail.state},\n${detail.city},${detail.state}",
                    false
                )
            )
//        listAddress.add(ModelShippingAddress("Office","Kuleshwor - 12,\nLalitpur, Nepal",false))
        }
        adapterAddress.notifyDataSetChanged()
    }

    override fun onEditClicked(data: ModelShippingAddress, position: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, FragmentAddressUpdate()).addToBackStack(null).commit()
    }

}