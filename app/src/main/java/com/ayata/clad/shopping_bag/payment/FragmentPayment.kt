package com.ayata.clad.shopping_bag.payment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView.BufferType
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentCartPaymentBinding
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.shopping_bag.adapter.AdapterPaymentMethod
import com.ayata.clad.shopping_bag.model.ModelCheckout
import com.ayata.clad.shopping_bag.model.ModelPaymentMethod
import com.ayata.clad.shopping_bag.order_placed.FragmentOrderPlaced
import com.ayata.clad.shopping_bag.response.checkout.Cart
import com.ayata.clad.shopping_bag.viewmodel.CheckoutViewModel
import com.ayata.clad.shopping_bag.viewmodel.CheckoutViewModelFactory
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.view_all.FragmentViewAllProduct

import com.khalti.checkout.helper.Config

import com.khalti.checkout.helper.KhaltiCheckOut

import com.khalti.checkout.helper.OnCheckOutListener

import com.khalti.utils.Constant
import java.time.temporal.TemporalAmount


class FragmentPayment : Fragment(), AdapterPaymentMethod.OnItemClickListener {

    private lateinit var binding: FragmentCartPaymentBinding
    private lateinit var adapterPaymentMethod: AdapterPaymentMethod
    private var listPaymentMethod = ArrayList<ModelPaymentMethod>()
    private var PAYMENTNAME = ""
    private lateinit var viewModel: CheckoutViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartPaymentBinding.inflate(inflater, container, false)

//        (parentFragment as FragmentShoppingBag).paymentPage()
        setUpViewModel()
        initAppbar()
        initView()
        initRecycler()
        setTermText()
        return binding.root
    }
    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            CheckoutViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[CheckoutViewModel::class.java]
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Payment",
            textDescription = ""
        )
    }

    private fun initView() {
        binding.btnConfirm.setOnClickListener {
            //get address get carts
            var carts=ArrayList<ModelCheckout>()
            var addressId = 0
            arguments?.let {
                addressId = it.getInt("address", 0)
                carts = it.getSerializable("carts") as ArrayList<ModelCheckout>
                Log.d("insideargumats", "initView: ");
            }
            Log.d("testid", "initView: "+addressId+"  "+PAYMENTNAME+"\n"+carts.toString());
            //hit order api
//            checkoutOrder(addressId,123.22)

            parentFragmentManager.popBackStack("checkout", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, FragmentOrderPlaced())
                .addToBackStack(null)
                .commit()
        }
        if (PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case), true)) {
            binding.totalPrice.text = "${getString(R.string.rs)} 7800.0"
        } else {
            binding.totalPrice.text = "${getString(R.string.usd)} 890"
        }
    }
    private fun checkoutOrder(addressId:Int,amount: Double) {
        viewModel.checkoutOrder(
            PreferenceHandler.getToken(context).toString(),
            PAYMENTNAME,"", arrayListOf(),addressId,amount)
        viewModel.observeCheckoutOrder().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("testdata", "addToWishListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {

                        } catch (e: Exception) {
                            Log.d("testdata", "addToWishListAPI:Error ${e.message}")
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d("testdata", "addToWishListAPI:Error ${it.message}")
                }
            }
        })
    }


    private fun setTermText() {
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

    private fun initRecycler() {
        adapterPaymentMethod = AdapterPaymentMethod(context, listPaymentMethod, this)
        binding.recyclerView.apply {
            adapter = adapterPaymentMethod
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        preparePaymentMethod()
    }

    private fun preparePaymentMethod() {
        listPaymentMethod.clear()
        listPaymentMethod.add(ModelPaymentMethod("Khalti Wallet", "", false))
        listPaymentMethod.add(ModelPaymentMethod("Esewa Wallet", "", false))
        listPaymentMethod.add(ModelPaymentMethod("IME Pay", "", false))

        adapterPaymentMethod.notifyDataSetChanged()
    }

    override fun onPaymentMethodClicked(data: ModelPaymentMethod, position: Int) {
        for (item in listPaymentMethod) {
            item.isSelected = data == item
        }
        adapterPaymentMethod.notifyDataSetChanged()
        if (data.name.equals("Khalti Wallet", ignoreCase = true)) {
            PAYMENTNAME = "KHALTI"
            setForKhalti()
        }else if(data.name.equals("Esewa Wallet", ignoreCase = true)){
            PAYMENTNAME = "ESEWA"
        }else{
            PAYMENTNAME = "CASHONDELIVERY"
        }
    }

    private fun setForKhalti() {
        val productId = "Product ID"
        val product_name = "Main"
        val amount = 100L
        val khalti_merchant_id = "This is extra data"
        val khalti_merchant_extra = "merchant_extra"
        val map: MutableMap<String, Any> = HashMap()
        map[khalti_merchant_extra] = khalti_merchant_id
        val builder: Config.Builder =
            Config.Builder(Constant.pub, productId, product_name, amount, object :
                OnCheckOutListener {
                override fun onSuccess(data: MutableMap<String, Any>) {
                    Log.i("success", data.toString())
                }

                override fun onError(action: String, errorMap: MutableMap<String, String>) {
                    Log.i("error", errorMap.toString())
                }
            })
        //                .paymentPreferences(new ArrayList<PaymentPreference>() {{
//                    add(PaymentPreference.KHALTI);
//                    add(PaymentPreference.EBANKING);
//                    add(PaymentPreference.MOBILE_BANKING);
//                    add(PaymentPreference.CONNECT_IPS);
//                    add(PaymentPreference.SCT);
//                }})
//                .additionalData(map)
//                .productUrl("")
//                .mobile("9800000000");
        val config: Config = builder.build()
        val khaltiCheckOut = KhaltiCheckOut(requireContext(), config)
        khaltiCheckOut.show()
    }

}