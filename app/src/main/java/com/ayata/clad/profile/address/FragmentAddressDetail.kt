package com.ayata.clad.profile.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import com.ayata.clad.utils.MyLayoutInflater
import com.ayata.clad.utils.PreferenceHandler

class FragmentAddressDetail : Fragment(), AdapterAddress.OnItemClickListener {

    private lateinit var binding: FragmentAddressDetailBinding
    private lateinit var adapterAddress: AdapterAddress
    private var listAddress = ArrayList<ModelShippingAddress>()
    private lateinit var viewModel: AddressViewModel
    private lateinit var userData: Detail
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
        viewModel.getUserAddress(PreferenceHandler.getToken(requireContext()).toString())
        binding.llAddAddress.setOnClickListener {
            val fragment = FragmentAddressAdd()
            val bundle = Bundle()
            bundle.putBoolean("ship",false)
            fragment.arguments=bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment,fragment).addToBackStack(null).commit()
        }

        return binding.root
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            AddressViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[AddressViewModel::class.java]
        viewModel.observeUserAddress().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.progressBar.root.visibility = View.GONE
                    try {
                        hideError()
                        it.data?.details?.let { it1 -> prepareListAddress(it1) }
                    } catch (e: Exception) {
                        showError(e.message.toString())
                    }
                }

                Status.LOADING -> {
                    binding.progressBar.root.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    showError(it.message.toString())
                    binding.progressBar.root.visibility = View.GONE
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
//        if (listAddress.size >= 2) {
//            binding.addNewBtn.visibility = View.GONE
//            binding.llAddAddress.visibility = View.GONE
//        } else {
//            binding.addNewBtn.visibility = View.GONE
//            binding.llAddAddress.visibility = View.VISIBLE
//        }
    }

    private fun prepareListAddress(details: List<Detail>) {
        listAddress.clear()
        userData = details[0]
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
        if(details.size>=1){
            binding.llAddAddress.visibility=View.GONE
        }else{
            binding.llAddAddress.visibility=View.VISIBLE
        }
        adapterAddress.notifyDataSetChanged()
    }
    private fun showError(it: String) {
        MyLayoutInflater().onAddField(
            requireContext(),
            binding.root,
            R.layout.layout_error,
            R.drawable.ic_cart,
            "Error!",
            it
        )

    }

    private fun hideError() {
        if (binding.root.findViewById<LinearLayout>(R.id.layout_root) != null) {
            MyLayoutInflater().onDelete(
                binding.root,
                binding.root.findViewById(R.id.layout_root)
            )
        }
    }
    override fun onEditClicked(data: ModelShippingAddress, position: Int) {
        val fragment = FragmentAddressAdd()
        val bundle = Bundle()
        bundle.putBoolean("ship",false)
        bundle.putSerializable("data", userData)
        fragment.arguments=bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, fragment).addToBackStack(null).commit()
    }

}