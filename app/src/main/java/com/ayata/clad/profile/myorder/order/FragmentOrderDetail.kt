package com.ayata.clad.profile.myorder.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentOrderDetailBinding
import com.ayata.clad.profile.myorder.order.`return`.FragmentReturnForm
import com.ayata.clad.profile.myorder.order.cancel.CancelViewModel
import com.ayata.clad.profile.myorder.order.cancel.FragmentCancelForm
import com.ayata.clad.profile.myorder.order.response.Order
import com.ayata.clad.profile.myorder.viewmodel.OrderViewModel
import com.ayata.clad.profile.myorder.viewmodel.OrderViewModelFactory
import com.ayata.clad.utils.PreferenceHandler
import com.bumptech.glide.Glide

class FragmentOrderDetail : Fragment() {
    private  var list_orderTrack= ArrayList<ModelOrderTrack>()
    lateinit var binding: FragmentOrderDetailBinding
    private lateinit var viewModel: OrderViewModel
    private lateinit var cancelviewModel: CancelViewModel
    lateinit var o: Order
    lateinit var conditionalStatus: String

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

        observeCancelFormPost()
//        observeCancelOrder()
        binding.recyclerOrderTracker.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AdapterOrderTrack(requireContext(), list_orderTrack)
        }
        binding.btnCancelOrder.setOnClickListener {
            if(this::o.isInitialized) {
                val bundle = Bundle()
                bundle.putSerializable("order", o)
                val frag = FragmentCancelForm()
                val fragReturn = FragmentReturnForm()
                frag.arguments = bundle
                fragReturn.arguments = bundle
                if (o.is_cancellable) {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.enter_from_right,
                            R.anim.exit_to_left,
                            R.anim.enter_from_left,
                            R.anim.exit_to_right
                        )
                        .replace(
                            R.id.main_fragment,
                            frag
                        ).addToBackStack(null).commit()

                }
                if (o.is_returnable) {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.enter_from_right,
                            R.anim.exit_to_left,
                            R.anim.enter_from_left,
                            R.anim.exit_to_right
                        )
                        .replace(
                            R.id.main_fragment,
                            fragReturn
                        ).addToBackStack(null).commit()
                }
            }else{
                Log.d("testerror", "onCreateView: ");
            }


        }
        return binding.root
    }
//    private fun observeCancelOrder(){
//
//        viewModel.observeCancelOrderResponse().observe(viewLifecycleOwner, {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    binding.spinKit.visibility = View.GONE
//                    val jsonObject = it.data
//                    if (jsonObject != null) {
//                        try {
////                                val orderResponse =
////                                    Gson().fromJson<OrderResponse>(
////                                        jsonObject,
////                                        OrderResponse::class.java
////                                    )
////                                if (orderResponse.details != null) {
////                                    val detail = orderResponse.details
////                                    prepareOrder(detail)
////                                }
//                            Toast.makeText(
//                                requireContext(),
//                                jsonObject.get("message").toString(),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            parentFragmentManager.popBackStackImmediate()
//                        } catch (e: Exception) {
////                                Log.d("", "prepareAPI: ${e.message}")
//                        }
//                    }
//
//                }
//                Status.LOADING -> {
//                    binding.spinKit.visibility = View.VISIBLE
//                }
//                Status.ERROR -> {
//                    //Handle Error
//                    binding.spinKit.visibility = View.GONE
//                    if (it.message.equals("Unauthorized")) {
//
//                    } else {
//                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
//
//                    }
//                    Log.d("", "home: ${it.message}")
//                }
//            }
//        })
//    }

    private fun observeCancelFormPost() {
        cancelviewModel.getCancelDetails().observe(viewLifecycleOwner, {
            if (it) {
//                viewModel.cancelOrderApi(PreferenceHandler.getToken(context)!!, o.orderId)

            }
        })
    }

    private fun checkForShowingRespectiveButton() {
        if (o.is_cancellable && !o.is_returnable) {
            binding.btnCancelOrder.text = "Cancel Order"
            binding.btnCancelOrder.visibility = View.VISIBLE
        } else if (o.is_returnable && !o.is_cancellable) {
            binding.btnCancelOrder.visibility = View.VISIBLE
            binding.btnCancelOrder.setText("Return Order")
        } else if (!o.is_cancellable && !o.is_returnable) {
            binding.btnCancelOrder.visibility = View.GONE
        }
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            OrderViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[OrderViewModel::class.java]
        cancelviewModel = ViewModelProviders.of(requireActivity()).get(CancelViewModel::class.java)

    }


    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = getString(R.string.order_details),
            textDescription = ""
        )
    }

    private fun populateData() {
        Log.d("testarguments", "populateData: " + arguments);
        arguments?.let {
            o = it?.getSerializable("order") as Order
            binding.address.text = o.shippingAddress
            binding.titleAddress.text = "Receiver: ${o.receiverName}"
            binding.phone.text = o.contactNumber
            binding.textView30.text = "Tracking ID: ${o.code}"
            if (PreferenceHandler.getCurrency(context).equals("npr", true)) {
                binding.include.price.text =
                    getString(R.string.rs) + "${o.products.variant.nprPrice}"
            } else {
                binding.include.price.text =
                    getString(R.string.usd) + "${o.products.variant.dollarPrice}"
            }
            binding.include.name.text = o.products.name
//            //date
//            try {
//                val dateformat = SimpleDateFormat("yyyy-MM-ddTHH:mm:ss")
//                val output_dateformat = SimpleDateFormat("MMMM dd, yyyy")
//                val date = dateformat.parse(o.createdAt)
//                val myFormatedDate = output_dateformat.format(date)
//                binding.textView29.text = "Ordered on " + myFormatedDate
//            } catch (e: Exception) {
//
//            }
            binding.textView29.text = "Ordered on " + o.createdAt
            binding.include.itemId.text = "Item ID: ${o.products.productId}"
            Glide.with(requireContext()).load(o.products.imageUrl).into(binding.include.image)
            binding.include.description.text =
                "${o.products.variant.size?.let { "Size: " + it + "/ " } ?: run { "" }}Colour: ${o.products.variant.color} / Qty: ${o.products.quantity}"


            //order status
            conditionalStatus = ModelOrderTrack.ORDER_TYPE_PLACED
            list_orderTrack = ArrayList<ModelOrderTrack>()
//        val titles = arrayOf(
//            ModelOrderTrack.ORDER_TYPE_PLACED,
//            ModelOrderTrack.ORDER_TYPE_DISPATCHED,
//            ModelOrderTrack.ORDER_TYPE_TRANSIT,
//            ModelOrderTrack.ORDER_TYPE_DELIVERED,
//            "",
//            "",
//            ""
//        )
//
//        val descriptions = arrayOf(
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            ""
//        )
            val title = arrayListOf<String>()
            val desc = arrayListOf<String>()
            try {
                for (i in 0 until o.orderStatus.size) {
                    title.add(o.orderStatus[i].status)
                    desc.add(o.orderStatus[i].date ?: "")
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }

            conditionalStatus = o.currentStatus
            Log.d("statusonditional", "populateData: " + conditionalStatus);

            title.reverse()
            desc.reverse()


            val colorInComplete: Int = R.color.colorGray
            val colorCompleted: Int = R.color.colorGray
            val colorCurrent: Int = R.color.colorBlack
            var setNone = false
            for (i in title.indices) {
                if (title[i].toLowerCase().trim { it <= ' ' } != conditionalStatus.toLowerCase()
                        .trim { it <= ' ' }) {
                    if (setNone) {
                        //set rest to grayed out
                        list_orderTrack.add(
                            ModelOrderTrack(
                                title[i],
                                desc[i], ModelOrderTrack.ORDER_TYPE_NONE, colorInComplete, false
                            )
                        )
                    } else {
                        list_orderTrack.add(
                            ModelOrderTrack(
                                title[i],
                                desc[i], title[i], colorCompleted, false
                            )
                        )
                    }
                } else {
                    list_orderTrack.add(
                        ModelOrderTrack(
                            title[i],
                            desc[i], title[i], colorCurrent, true
                        )
                    )
                    setNone = true //set it to true after we found the exact title
                }
            }

            checkForShowingRespectiveButton()
        }

    }
}