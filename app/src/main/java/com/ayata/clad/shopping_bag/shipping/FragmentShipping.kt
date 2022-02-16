package com.ayata.clad.shopping_bag.shipping

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.core.text.underline
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentCartShippingBinding
import com.ayata.clad.profile.address.FragmentAddressAdd
import com.ayata.clad.profile.address.FragmentAddressUpdate
import com.ayata.clad.shopping_bag.FragmentShoppingBag
import com.ayata.clad.shopping_bag.adapter.AdapterShippingAddress
import com.ayata.clad.shopping_bag.model.ModelShippingAddress
import com.ayata.clad.shopping_bag.payment.FragmentPayment
import com.ayata.clad.utils.PreferenceHandler


class FragmentShipping : Fragment(),AdapterShippingAddress.OnItemClickListener {

    private lateinit var binding:FragmentCartShippingBinding

    private lateinit var adapterShippingAddress: AdapterShippingAddress
    private var listAddress=ArrayList<ModelShippingAddress>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCartShippingBinding.inflate(inflater, container, false)

//        (parentFragment as FragmentShoppingBag).shippingPage()
        initAppbar()
        initView()
        initRecycler()
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Shipping",
            textDescription = ""
        )
    }

    private fun initView(){
        binding.textTerms.text=SpannableStringBuilder().append("By placing an order you agree to our \n")
            .underline {color(ContextCompat.getColor(requireContext(),R.color.colorBlack)) { append("Terms and Conditions.") }}

        binding.textTerms.setOnClickListener {
            val url = getString(R.string.terms_url)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        binding.btnProceed.setOnClickListener {
//            fragment_shopping
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, FragmentPayment())
                .addToBackStack("shipping").commit()
        }

        binding.addNewBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, FragmentAddressAdd()).addToBackStack(null).commit()
        }

        if(PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case),true)){
            binding.shippingPrice.text="${getString(R.string.rs)} 100"
            binding.promoPrice.text="${getString(R.string.rs)} 200"
            binding.subTotal.text="${getString(R.string.rs)} 7900"
            binding.totalPrice.text="${getString(R.string.rs)} 7800.0"
        }else{
            binding.shippingPrice.text="${getString(R.string.usd)} 60"
            binding.promoPrice.text="${getString(R.string.usd)} 20"
            binding.subTotal.text="${getString(R.string.usd)} 850"
            binding.totalPrice.text="${getString(R.string.usd)} 890"
        }
    }

    private fun initRecycler(){

        adapterShippingAddress= AdapterShippingAddress(context,listAddress,this)
        binding.recyclerView.apply {
            adapter=adapterShippingAddress
            layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        }

        prepareListAddress()
        if(listAddress.size>=2){
            binding.addNewBtn.visibility=View.GONE
        }else{
            binding.addNewBtn.visibility=View.VISIBLE
        }
    }

    private fun prepareListAddress(){

        listAddress.clear()
        listAddress.add(ModelShippingAddress("Home","New Baneshwor - 10,\nKathmandu, Nepal",true))
        listAddress.add(ModelShippingAddress("Office","Kuleshwor - 12,\nLalitpur, Nepal",false))

        adapterShippingAddress.notifyDataSetChanged()
    }

    override fun onAddressSelected(data: ModelShippingAddress, position: Int) {
        for(item in listAddress){
            item.isSelected = item.equals(data)
        }
        adapterShippingAddress.notifyDataSetChanged()
    }

    override fun onEditClicked(data: ModelShippingAddress, position: Int) {
//        Toast.makeText(context,"Edit--${data.name}",Toast.LENGTH_SHORT).show()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, FragmentAddressUpdate()).addToBackStack(null).commit()
    }

}