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
import com.ayata.clad.shopping_bag.adapter.AdapterPaymentMethod
import com.ayata.clad.shopping_bag.model.ModelCheckout
import com.ayata.clad.shopping_bag.model.ModelPaymentMethod
import com.ayata.clad.shopping_bag.order_placed.FragmentOrderPlaced
import com.ayata.clad.shopping_bag.payment.response.PaymentResponse
import com.ayata.clad.shopping_bag.payment.viewmodel.PaymentViewModel
import com.ayata.clad.shopping_bag.payment.viewmodel.PaymentViewModelFactory
import com.ayata.clad.shopping_bag.response.checkout.CartResponse
import com.ayata.clad.utils.PreferenceHandler
import com.google.gson.Gson
import com.khalti.checkout.helper.Config
import com.khalti.checkout.helper.KhaltiCheckOut
import com.khalti.checkout.helper.OnCheckOutListener
import com.khalti.utils.Constant


class FragmentPayment : Fragment(), AdapterPaymentMethod.OnItemClickListener {

    private lateinit var binding: FragmentCartPaymentBinding
    private lateinit var adapterPaymentMethod: AdapterPaymentMethod
    private var listPaymentMethod = ArrayList<ModelPaymentMethod>()
    private var PAYMENTID = ""
    private var PAYMENTTOKEN = ""
    private lateinit var viewModel: PaymentViewModel
    private lateinit var cartResponse: CartResponse


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartPaymentBinding.inflate(inflater, container, false)

//        (parentFragment as FragmentShoppingBag).paymentPage()
        setUpViewModel()
        initBundle()
        initAppbar()
        initView()
        initRecycler()
        getPaymentApi()
        setTermText()
        return binding.root
    }

    private fun getPaymentApi() {
        viewModel.getPaymentApi(
            PreferenceHandler.getToken(context).toString()
        )
        viewModel.observePaymentApi().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.spinKit.visibility = View.GONE
                    Log.d("testdata", "addToWishListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            var paymentResponse =
                                Gson().fromJson(jsonObject, PaymentResponse::class.java)
                            preparePaymentMethod(paymentResponse.gateways)

                        } catch (e: Exception) {
                            Log.d("testdata", "addToWishListAPI:Error ${e.message}")
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
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
                    Log.d("testdata", "addToWishListAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun initBundle() {
        arguments?.let {
            cartResponse = it.getSerializable("totals") as CartResponse
        }
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            PaymentViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[PaymentViewModel::class.java]
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
            var carts = ArrayList<ModelCheckout>()
            var addressId = 0
            var addressType = ""
            arguments?.let {
                addressId = it.getInt("address", 0)
                carts = it.getSerializable("carts") as ArrayList<ModelCheckout>
                Log.d("insideargumats", "initView: ");
                addressType = it.getString("address_type", "")
            }
//            Log.d("testid", "initView: "+addressId+"  "+PAYMENTNAME+"\n"+carts.toString());
            //hit order api
            checkoutOrder(addressId, addressType, carts)
        }
        if (PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case), true)) {
            binding.totalPrice.text = "${getString(R.string.rs)} ${cartResponse.cartGrandTotalNpr}"
        } else {
            binding.totalPrice.text =
                "${getString(R.string.usd)} ${cartResponse.cartGrandTotalDollar}"
        }
    }

    private fun checkoutOrder(addressId: Int, addressType: String, carts: List<ModelCheckout>) {
        val cartIdList = carts.map { it.cartId }
        if(PAYMENTID.isEmpty()){
            Toast.makeText(context,"Select at least one payment gateway.",Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.checkoutOrder(
            PreferenceHandler.getToken(context).toString(),
            PAYMENTID,
            PAYMENTTOKEN,
            cartIdList,
            addressId,
            addressType,
            cartResponse.cartGrandTotalNpr
        )
        viewModel.observeCheckoutOrder().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.spinKit.visibility = View.GONE
                    Log.d("testdata", "addToWishListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            parentFragmentManager.popBackStack(
                                "checkout",
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.main_fragment, FragmentOrderPlaced())
                                .addToBackStack(null)
                                .commit()
                        } catch (e: Exception) {
                            Log.d("testdata", "addToWishListAPI:Error ${e.message}")
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
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
//        preparePaymentMethod()
    }

    private fun preparePaymentMethod(gateways: List<ModelPaymentMethod>) {
        listPaymentMethod.clear()
        listPaymentMethod.addAll(gateways)
        adapterPaymentMethod.notifyDataSetChanged()
    }

    override fun onPaymentMethodClicked(data: ModelPaymentMethod, position: Int) {
        for (item in listPaymentMethod) {
            item.isSelected = data == item
        }
        if (data.name.equals("Khalti Wallet", ignoreCase = true)) {
//             "KHALTI"
            PAYMENTID = data.name
            setForKhalti()
        } else if (data.name.equals("Esewa Wallet", ignoreCase = true)) {
            //esewa
            PAYMENTID = data.name
        } else {
            //cash
            PAYMENTID = data.name
        }
        adapterPaymentMethod.notifyDataSetChanged()

    }

    private fun setForKhalti() {
        val productId = "ProductID"
        val product_name = "Main"
        val amount = cartResponse.cartGrandTotalNpr.toLong()
//        val khalti_merchant_id = "This is extra data"
//        val khalti_merchant_extra = "merchant_extra"
//        val map: MutableMap<String, Any> = HashMap()
//        map[khalti_merchant_extra] = khalti_merchant_id

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
        try {
            val builder: Config.Builder =
                Config.Builder(Constant.pub, productId, product_name, amount, object :
                    OnCheckOutListener {
                    override fun onSuccess(data: MutableMap<String, Any>) {
                        PAYMENTTOKEN = data.get("token").toString()
                    }

                    override fun onError(action: String, errorMap: MutableMap<String, String>) {
                        Log.i("errorkhaltti", errorMap.toString())
                    }
                })
            val config: Config = builder.build()
            val khaltiCheckOut = KhaltiCheckOut(requireContext(), config)
            khaltiCheckOut.show()
        } catch (e: Exception) {
            Log.d("testkhalti", "setForKhalti: " + e.message);
        }

    }

}