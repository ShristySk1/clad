package com.ayata.clad.profile.myorder.order

import android.os.Bundle
import android.util.Log
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
import com.ayata.clad.databinding.FragmentOrderDetailBinding
import com.ayata.clad.profile.myorder.order.response.Order
import com.ayata.clad.profile.myorder.viewmodel.OrderViewModel
import com.ayata.clad.profile.myorder.viewmodel.OrderViewModelFactory
import com.ayata.clad.utils.PreferenceHandler
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat

class FragmentOrderDetail : Fragment() {
    private lateinit var list_orderTrack: ArrayList<ModelOrderTrack>
    lateinit var binding: FragmentOrderDetailBinding
    private lateinit var viewModel: OrderViewModel
    lateinit var o: Order
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentOrderDetailBinding.inflate(inflater, container, false)
        initAppbar()
        setUpViewModel()
        populateData()
        binding.recyclerOrderTracker.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AdapterOrderTrack(requireContext(), list_orderTrack)
        }

        if (PreferenceHandler.getCurrency(context).equals("npr", true)) {
            binding.include.price.text = getString(R.string.rs) + " 7850"
        } else {
            binding.include.price.text = getString(R.string.usd) + " 120"
        }
        binding.btnCancelOrder.setOnClickListener {
            viewModel.cancelOrderApi(PreferenceHandler.getToken(context)!!, o.orderId)
            viewModel.observeCancelOrderResponse().observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.spinKit.visibility = View.GONE
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
                                    jsonObject.get("message").toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
//                                Log.d("", "prepareAPI: ${e.message}")
                            }
                        }

                    }
                    Status.LOADING -> {
                        binding.spinKit.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        //Handle Error
                        binding.spinKit.visibility = View.GONE
                        if (it.message.equals("Unauthorized")) {

                        } else {
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()

                        }
                        Log.d("", "home: ${it.message}")
                    }
                }
            })
        }
        return binding.root
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            OrderViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[OrderViewModel::class.java]
    }


    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = true, isClear = false,
            textTitle = getString(R.string.order_details),
            textDescription = "X935-12SC"
        )
    }

    private fun populateData() {

        arguments.let {
            o = it?.getSerializable("order") as Order
            binding.address.text = o.shippingAddress
            binding.titleAddress.text = "Receiver: ${o.receiverName}"
            binding.phone.text = o.contactNumber
            binding.textView30.text = "Tracking ID: ${o.code}"
            binding.include.name.text = o.products.name
            //date
            try {
                val dateformat = SimpleDateFormat("yyyy-MM-ddTHH:mm:ss")
                val output_dateformat = SimpleDateFormat("MMMM dd, yyyy")
                val date = dateformat.parse(o.createdAt)
                val myFormatedDate = output_dateformat.format(date)
                binding.textView29.text ="Ordered on "+ myFormatedDate
            } catch (e: Exception) {

            }

            binding.include.itemId.text = "Item ID: ${o.products.productId}"
            Glide.with(requireContext()).load(o.products.imageUrl).into(binding.include.image)
            binding.include.description.text =
                "${o.products.variant.size?.let { "Size: "+it +"/ "}?:run{""}}Colour: ${o.products.variant.color} / Qty: ${o.products.quantity}"
        }

        //order status
        var conditionalStatus = ModelOrderTrack.ORDER_TYPE_PLACED
        list_orderTrack = ArrayList<ModelOrderTrack>()
        val titles = arrayOf(
            ModelOrderTrack.ORDER_TYPE_PLACED,
            ModelOrderTrack.ORDER_TYPE_DISPATCHED,
            ModelOrderTrack.ORDER_TYPE_TRANSIT,
            ModelOrderTrack.ORDER_TYPE_DELIVERED
        )

        val descriptions = arrayOf(
            "",
            "",
            "",
            ""
        )
        for (i in 0 until o.orderStatus.size) {
            titles[i] = o.orderStatus[i].status
            descriptions[i] = o.orderStatus[i].date
        }
        conditionalStatus = o.orderStatus[(o.orderStatus.size) - 1].status
        Log.d("statusonditional", "populateData: " + conditionalStatus);

        titles.reverse()
        descriptions.reverse()


        val colorInComplete: Int = R.color.colorGray
        val colorCompleted: Int = R.color.colorGray
        val colorCurrent: Int = R.color.colorBlack
        var setNone = false
        for (i in titles.indices) {
            if (titles[i].toLowerCase().trim { it <= ' ' } != conditionalStatus.toLowerCase()
                    .trim { it <= ' ' }) {
                if (setNone) {
                    //set rest to grayed out
                    list_orderTrack.add(
                        ModelOrderTrack(
                            titles[i],
                            descriptions[i], ModelOrderTrack.ORDER_TYPE_NONE, colorInComplete, false
                        )
                    )
                } else {
                    list_orderTrack.add(
                        ModelOrderTrack(
                            titles[i],
                            descriptions[i], titles[i], colorCompleted, false
                        )
                    )
                }
            } else {
                list_orderTrack.add(
                    ModelOrderTrack(
                        titles[i],
                        descriptions[i], titles[i], colorCurrent, true
                    )
                )
                setNone = true //set it to true after we found the exact title
            }
        }
    }
}