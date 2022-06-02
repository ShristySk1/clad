package com.ayata.clad.profile.myorder.order.cancel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentCancelFormBinding
import com.ayata.clad.profile.address.CustomArrayAdapter
import com.ayata.clad.profile.address.ModelTest
import com.ayata.clad.profile.myorder.order.response.Order
import com.ayata.clad.profile.myorder.viewmodel.OrderViewModel
import com.ayata.clad.profile.myorder.viewmodel.OrderViewModelFactory
import com.ayata.clad.utils.*
import com.bumptech.glide.Glide


class FragmentCancelForm : Fragment() {
    lateinit var binding: FragmentCancelFormBinding
    private lateinit var viewModel: OrderViewModel
    lateinit var cancelViewmodel: CancelViewModel
    lateinit var o: Order
    private var reason_ = "";
    lateinit var progressDialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentCancelFormBinding.inflate(inflater, container, false)
        setSpinner()
        initView()
        setUpViewModel()
        observeCancelOrder()
        binding.btnCancelOrder.setOnClickListener {
            val comment = binding.tvDescription.text.toString()
            //validate comment
//            if (!binding.tvDescription.validateEditText()) {
//                return@setOnClickListener
//            }
            //send comment and reason in post request
//            Handler().postDelayed({
//                progressDialog.dismiss()
//                parentFragmentManager.popBackStack(
//                    "order_list",
//                    FragmentManager.POP_BACK_STACK_INCLUSIVE
//                )
//            }, 3000)
            viewModel.cancelOrderApi(
                PreferenceHandler.getToken(context)!!,
                o.orderId,
                comment,
                reason_,
            )
        }
        return binding.root
    }

    private fun observeCancelOrder() {

        viewModel.observeCancelOrderResponse().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {

                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
//                                val orderResponse =
//                                    Gson().fromJson<OrderResponse>(
//                                        jsonObject,
//                                        OrderResponse::class.java
//                                    )
//                                if (orderResponse.details != null) {
//                                    val detail = orderResponse.details
//                                    prepareOrder(detail)
//                                }
                            Toast.makeText(
                                requireContext(),
                                jsonObject.get("message").toString().removeDoubleQuote(),
                                Toast.LENGTH_SHORT
                            ).show()
                            cancelViewmodel.setCancelDetails(true)
                            progressDialog.dismiss()
                            //directly go to order list fragment
                            parentFragmentManager.popBackStack(
                                "order_list",
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                        } catch (e: Exception) {
//                                Log.d("", "prepareAPI: ${e.message}")
                        }
                    }

                }
                Status.LOADING -> {
                    progressDialog = ProgressDialog.newInstance("", "")
                    progressDialog.show(parentFragmentManager, "cancel_progress")

                }
                Status.ERROR -> {
                    progressDialog.dismiss()
                    //Handle Error
                    if (it.message.equals("Unauthorized")) {

                    } else {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()

                    }
                    Log.d("", "home: ${it.message}")
                }
            }
        })
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            OrderViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[OrderViewModel::class.java]
        cancelViewmodel = ViewModelProviders.of(requireActivity()).get(CancelViewModel::class.java)

    }

    private fun initView() {
        arguments?.let {
            o = it?.getSerializable("order") as Order
            if (PreferenceHandler.getCurrency(context).equals("npr", true)) {
                binding.price.text = getString(R.string.rs) + "${o.products.variant.nprPrice}"
            } else {
                binding.price.text = getString(R.string.usd) + "${o.products.variant.dollarPrice}"
            }
            binding.name.text = o.products.name
            binding.itemId.text = "Item ID: ${o.products.productId}"
            Glide.with(requireContext()).load(o.products.imageUrl).into(binding.image)
            binding.description.text =
                "${o.products.variant.size?.let { "Size: " + it + "/ " } ?: run { "" }}Colour: ${o.products.variant.color} / Qty: ${o.products.quantity}"
        }
//        binding.text10.setDifferentColor(requireContext(), "Comment", " *")
    }

    private fun setSpinner() {
        val listReason =
            arrayListOf<ModelTest>(
                ModelTest(arrayListOf(), "Change of mind"),
                ModelTest(arrayListOf(), "Change of Delivery Address"),
//                ModelTest(arrayListOf(), "Shipping Fees"),
                ModelTest(arrayListOf(), "Decided for alternative product"),
                ModelTest(arrayListOf(), "Found cheaper elsewhere"),
                ModelTest(arrayListOf(), "Forgot to use voucher"),
                ModelTest(arrayListOf(), "Delivery time is too long"),
                ModelTest(arrayListOf(), "Duplicate order")
            )
        if (binding.spinner != null) {
            val adapter =
                CustomArrayAdapter(
                    requireContext(),
                    R.layout.spinner_custom,
                    R.id.text_view,
                    listReason
                )
            binding.spinner.adapter = adapter
            binding.spinner.setPrompt("Select Reason");
            binding.spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    reason_ = listReason[position].state
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

    }
}