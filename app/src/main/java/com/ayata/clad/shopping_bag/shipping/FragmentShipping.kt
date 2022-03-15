package com.ayata.clad.shopping_bag.shipping

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.core.text.underline
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentCartShippingBinding
import com.ayata.clad.profile.address.FragmentAddressAdd
import com.ayata.clad.profile.address.FragmentAddressUpdate
import com.ayata.clad.profile.address.response.Detail
import com.ayata.clad.profile.address.viewmodel.AddressViewModel
import com.ayata.clad.profile.address.viewmodel.AddressViewModelFactory
import com.ayata.clad.shopping_bag.adapter.AdapterShippingAddress
import com.ayata.clad.shopping_bag.model.ModelShippingAddress
import com.ayata.clad.shopping_bag.payment.FragmentPayment
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.PreferenceHandler
import java.nio.BufferUnderflowException


class FragmentShipping : Fragment(){

    private lateinit var binding: FragmentCartShippingBinding

    private lateinit var adapterShippingAddress: AdapterShippingAddress
    private var listAddress = ArrayList<ModelShippingAddress>()

    //address2
    private lateinit var adapterShippingAddress2: AdapterShippingAddress
    private var listAddress2 = ArrayList<ModelShippingAddress>()

    private lateinit var viewModel: AddressViewModel
    private lateinit var shipData:Detail
    private var isFirstChecked=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            AddressViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[AddressViewModel::class.java]
        viewModel.getUserAddress(
            Constants.Bearer + " " + PreferenceHandler.getToken(
                requireContext()
            )
        )
        viewModel.getShippingAddress(
            Constants.Bearer + " " + PreferenceHandler.getToken(
                requireContext()
            )
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartShippingBinding.inflate(inflater, container, false)
        initAppbar()
        initView()
        setUpViewModel()
        return binding.root
    }
    private fun setUpViewModel() {

        viewModel.observeShippingAddress().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.spinKit.visibility = View.GONE
                    try {
                        it.data?.details?.let { it1 -> prepareListAddress2(it1) }
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
        viewModel.observeUserAddress().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.spinKit.visibility = View.GONE
                    try {
                        it.data?.details?.let { it1 -> prepareListAddress1(it1) }
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
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Shipping",
            textDescription = ""
        )
    }

    private fun initView() {
        binding.textTerms.text =
            SpannableStringBuilder().append("By placing an order you agree to our \n")
                .underline {
                    color(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorBlack
                        )
                    ) { append("Terms and Conditions.") }
                }

        binding.textTerms.setOnClickListener {
            val url = getString(R.string.terms_url)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        binding.btnProceed.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, FragmentPayment())
                .addToBackStack("shipping").commit()
        }

        binding.addNewBtn.setOnClickListener {
            val fragment= FragmentAddressAdd()
            val bundle=Bundle()
            bundle.putBoolean("ship",true)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment,fragment).addToBackStack(null).commit()
        }

        binding.recyclerView.rootContainer.setOnClickListener {
            isFirstChecked=true
           setCheckBox()
        }
        binding.recyclerView2.rootContainer.setOnClickListener {
            isFirstChecked=false
            setCheckBox()
        }
        binding.recyclerView2.editBtn.setOnClickListener {
            val fragment= FragmentAddressAdd()
            val bundle=Bundle()
            bundle.putBoolean("ship",true)
            bundle.putSerializable("data",shipData)
            fragment.arguments=bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment,fragment).addToBackStack(null).commit()
        }
        if (PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case), true)) {
            binding.shippingPrice.text = "${getString(R.string.rs)} 100"
            binding.promoPrice.text = "${getString(R.string.rs)} 200"
            binding.subTotal.text = "${getString(R.string.rs)} 7900"
            binding.totalPrice.text = "${getString(R.string.rs)} 7800.0"
        } else {
            binding.shippingPrice.text = "${getString(R.string.usd)} 60"
            binding.promoPrice.text = "${getString(R.string.usd)} 20"
            binding.subTotal.text = "${getString(R.string.usd)} 850"
            binding.totalPrice.text = "${getString(R.string.usd)} 890"
        }
    }

    private fun setCheckBox() {
        if( isFirstChecked){
            binding.recyclerView.checkBox.isChecked=true
            binding.recyclerView2.checkBox.isChecked=false
        }else{
            binding.recyclerView.checkBox.isChecked=false
            binding.recyclerView2.checkBox.isChecked=true
        }

    }

    private fun prepareListAddress1(details: List<Detail>) {
        listAddress.clear()
        if(details.size>0) {
            binding.recyclerView.editBtn.visibility=View.GONE
            binding.recyclerView.rootContainer.visibility=View.VISIBLE
            binding.recyclerView.titleAddress.text=details[0].title
            binding.recyclerView.address.text=details[0].streetName
            setCheckBox()
        }else{
            binding.recyclerView.rootContainer.visibility=View.GONE
        }
    }
    private fun prepareListAddress2(details: List<Detail>) {
        if(details.size>0) {
            shipData=details[0]
            binding.recyclerView2.rootContainer.visibility=View.VISIBLE
            binding.recyclerView2.titleAddress.text=details[0].title
            binding.recyclerView2.address.text=details[0].streetName
//            binding.recyclerView2.checkBox.visibility=View.GONE
            binding.addNewBtn.visibility=View.GONE

        }else{
            binding.addNewBtn.visibility=View.VISIBLE
            binding.recyclerView.rootContainer.visibility=View.GONE

        }
    }

//    override fun onAddressSelected(data: ModelShippingAddress, position: Int) {
//        for (item in listAddress) {
//            item.isSelected = item.equals(data)
//        }
//        adapterShippingAddress.notifyDataSetChanged()
//    }
//
//    override fun onEditClicked(data: ModelShippingAddress, position: Int) {
////        Toast.makeText(context,"Edit--${data.name}",Toast.LENGTH_SHORT).show()
//        requireActivity().supportFragmentManager.beginTransaction()
//            .replace(R.id.main_fragment, FragmentAddressUpdate()).addToBackStack(null).commit()
//    }

}