package com.ayata.clad.shopping_bag.shipping

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.core.text.underline
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentShippingBinding
import com.ayata.clad.shopping_bag.FragmentShoppingBag
import com.ayata.clad.shopping_bag.adapter.AdapterShippingAddress
import com.ayata.clad.shopping_bag.checkout.FragmentCheckout
import com.ayata.clad.shopping_bag.model.ModelShippingAddress
import com.ayata.clad.shopping_bag.payment.FragmentPayment


class FragmentShipping : Fragment(),AdapterShippingAddress.OnItemClickListener {

    private lateinit var binding:FragmentShippingBinding

    private lateinit var adapterShippingAddress: AdapterShippingAddress
    private var listAddress=ArrayList<ModelShippingAddress>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentShippingBinding.inflate(inflater, container, false)

        (parentFragment as FragmentShoppingBag).shippingPage()
        initView()
        initRecycler()
        return binding.root
    }

    private fun initView(){
        binding.textTerms.text=SpannableStringBuilder().append("By placing an order you agree to our \n")
            .underline {color(ContextCompat.getColor(requireContext(),R.color.black)) { append("Terms and Conditions.") }}

        binding.textTerms.setOnClickListener {
            val url = getString(R.string.terms_url)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        binding.btnProceed.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_shopping, FragmentPayment())
                .addToBackStack(null).commit()
        }

        binding.addNewBtn.setOnClickListener {
            Toast.makeText(context,"Add address",Toast.LENGTH_SHORT).show()
        }

        binding.shippingPrice.text="Rs. 60"
        binding.promoPrice.text="Rs. 100"
        binding.subTotal.text="Rs. 9840"
        binding.totalPrice.text="Rs. 9800"
    }

    private fun initRecycler(){

        adapterShippingAddress= AdapterShippingAddress(context,listAddress,this)
        binding.recyclerView.apply {
            adapter=adapterShippingAddress
            layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        }

        prepareListAddress()
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
        Toast.makeText(context,"Edit--${data.name}",Toast.LENGTH_SHORT).show()
    }

}