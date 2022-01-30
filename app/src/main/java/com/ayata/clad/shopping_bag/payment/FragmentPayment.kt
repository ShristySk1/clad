package com.ayata.clad.shopping_bag.payment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView.BufferType
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentPaymentBinding
import com.ayata.clad.shopping_bag.FragmentShoppingBag
import com.ayata.clad.shopping_bag.adapter.AdapterPaymentMethod
import com.ayata.clad.shopping_bag.model.ModelPaymentMethod
import com.ayata.clad.shopping_bag.order_placed.FragmentOrderPlaced
import com.ayata.clad.shopping_bag.shipping.FragmentShipping
import com.ayata.clad.utils.PreferenceHandler


class FragmentPayment : Fragment(), AdapterPaymentMethod.OnItemClickListener {

    private lateinit var binding:FragmentPaymentBinding
    private lateinit var adapterPaymentMethod:AdapterPaymentMethod
    private var listPaymentMethod=ArrayList<ModelPaymentMethod>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentPaymentBinding.inflate(inflater, container, false)

        (parentFragment as FragmentShoppingBag).paymentPage()
        initView()
        initRecycler()
        setTermText()

        return binding.root
    }

    private fun initView(){
        binding.btnConfirm.setOnClickListener {
            activity?.supportFragmentManager!!.beginTransaction()
                .replace(R.id.main_fragment,FragmentOrderPlaced())
                .addToBackStack(null)
                .commit()
            parentFragmentManager.popBackStack("checkout", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        if(PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case),true)){
            binding.totalPrice.text="${getString(R.string.rs)} 7800.0"
        }else{
            binding.totalPrice.text="${getString(R.string.usd)} 890"
        }
    }

    private fun setTermText(){
        val spanTxt = SpannableStringBuilder(
            "By clicking “Proceed to payment” I approve the terms for the "
        )
        spanTxt.append("Clad Shopping Service")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val url = getString(R.string.clad_shopping_service_url)
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }, spanTxt.length - "Clad Shopping Service".length, spanTxt.length, 0)
        spanTxt.append(" and confirm I have read ")
        spanTxt.append("Clad’s Privacy Notice")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val url = getString(R.string.clad_privacy_notice_url)
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }, spanTxt.length - "Clad’s Privacy Notice".length, spanTxt.length, 0)

        spanTxt.append(" and ")
        spanTxt.append("Cookie Notice")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val url = getString(R.string.cookie_notice_url)
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }, spanTxt.length - "Cookie Notice".length, spanTxt.length, 0)
        spanTxt.append(".")

        binding.textTerms.movementMethod = LinkMovementMethod.getInstance()
        binding.textTerms.setText(spanTxt, BufferType.SPANNABLE)
    }

    private fun initRecycler(){

        adapterPaymentMethod= AdapterPaymentMethod(context,listPaymentMethod,this)
        binding.recyclerView.apply {
            adapter=adapterPaymentMethod
            layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        }

        preparePaymentMethod()

    }

    private fun preparePaymentMethod(){
        listPaymentMethod.clear()
        listPaymentMethod.add(ModelPaymentMethod("Khalti Wallet","",true))
        listPaymentMethod.add(ModelPaymentMethod("Esewa Wallet","",false))
        listPaymentMethod.add(ModelPaymentMethod("IME Pay","",false))

        adapterPaymentMethod.notifyDataSetChanged()
    }

    override fun onPaymentMethodClicked(data: ModelPaymentMethod, position: Int) {
        for(item in listPaymentMethod){
            item.isSelected = data == item
        }
        adapterPaymentMethod.notifyDataSetChanged()
    }


}