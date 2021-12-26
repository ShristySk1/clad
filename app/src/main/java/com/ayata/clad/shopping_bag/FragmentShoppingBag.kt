package com.ayata.clad.shopping_bag

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.DialogShoppingSizeBinding
import com.ayata.clad.databinding.FragmentShoppingBagBinding
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.checkout.FragmentCheckout
import com.ayata.clad.shopping_bag.model.ModelCircleText
import com.ayata.clad.shopping_bag.payment.FragmentPayment
import com.ayata.clad.shopping_bag.shipping.FragmentShipping
import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentShoppingBag : Fragment() {

    private lateinit var binding: FragmentShoppingBagBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentShoppingBagBinding.inflate(inflater, container, false)
        checkoutPage()
        initAppbar()
        initView()
        (activity as MainActivity).removeAllFragment()
        Log.d("BackCheck", "onCreateView: here")
        if(binding.fragmentShopping != null) {
            if (savedInstanceState == null) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.fragment_shopping, FragmentCheckout()).commit()
            }
        }

        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(getString(R.string.shopping_bag),
            isSearch = false,
            isProfile = true,
            isClose = false
        )
    }

    private fun initView(){
        binding.layoutEmpty.visibility=View.GONE
        binding.layoutFilled.visibility=View.VISIBLE

        binding.btnBrowse.setOnClickListener {
            (activity as MainActivity).openFragmentShop()
        }
    }

    fun checkoutPage(){
        binding.textMiddle.typeface= Typeface.DEFAULT
        binding.textRight.typeface= Typeface.DEFAULT
        binding.textLeft.typeface= Typeface.DEFAULT_BOLD
        binding.progressBar.progress=10
    }

    fun paymentPage(){
        binding.textMiddle.typeface= Typeface.DEFAULT_BOLD
        binding.textRight.typeface= Typeface.DEFAULT_BOLD
        binding.textLeft.typeface= Typeface.DEFAULT_BOLD
        binding.progressBar.progress=100
    }

    fun shippingPage(){
        binding.textMiddle.typeface= Typeface.DEFAULT_BOLD
        binding.textRight.typeface= Typeface.DEFAULT
        binding.textLeft.typeface= Typeface.DEFAULT_BOLD
        binding.progressBar.progress=50
    }

}