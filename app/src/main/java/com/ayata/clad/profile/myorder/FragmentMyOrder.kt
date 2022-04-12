package com.ayata.clad.profile.myorder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentMyorderBinding
import com.ayata.clad.profile.MyOrderRecyclerViewItem
import com.ayata.clad.profile.account.AccountViewModel
import com.ayata.clad.profile.myorder.order.FragmentOrderDetail
import com.ayata.clad.profile.myorder.order.cancel.CancelViewModel
import com.ayata.clad.profile.myorder.order.response.Detail
import com.ayata.clad.profile.myorder.order.response.OrderResponse
import com.ayata.clad.profile.myorder.viewmodel.OrderViewModel
import com.ayata.clad.profile.myorder.viewmodel.OrderViewModelFactory
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.MyLayoutInflater
import com.ayata.clad.utils.PreferenceHandler
import com.google.gson.Gson


class FragmentMyOrder : Fragment() {
    lateinit var binding: FragmentMyorderBinding
    private lateinit var viewModel: OrderViewModel
    private lateinit var cancelViewModel:CancelViewModel
    val myAdapter by lazy { AdapterOrders(requireContext(), listOf()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentMyorderBinding.inflate(inflater, container, false)

        setUpRecyclerView()
        getOrderApi()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    private fun getOrderApi() {
        viewModel.getOrderApi(PreferenceHandler.getToken(context)!!)
        viewModel.observeOrderResponse().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
//                    binding.spinKit.visibility = View.GONE
                    hideProgress()
                    hideError()
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val orderResponse =
                                Gson().fromJson<OrderResponse>(
                                    jsonObject,
                                    OrderResponse::class.java
                                )
                            if (orderResponse.details != null) {
                                val detail = orderResponse.details
                                if (detail.size > 0) {
                                    prepareOrder (detail)
                                } else {
                                    showError("Empty!", "No any order yet.")
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("", "prepareAPI: ${e.message}")
                        }
                    }

                }
                Status.LOADING -> {
                    showProgress()
                    hideError()
//                    binding.spinKit.visibility = View.VISIBLE

                }
                Status.ERROR -> {
                    hideProgress()
                    showError("Error!", it.message.toString())
                    //Handle Error
//                    binding.spinKit.visibility = View.GONE
                    if (it.message.equals("Unauthorized")) {

                    } else {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()

                    }
                    Log.d("", "home: ${it.message}")
                }
            }
        })
    }

    fun showProgress() {
        binding.progressBar.rootContainer.visibility = View.VISIBLE

    }

    fun hideProgress() {
        binding.progressBar.rootContainer.visibility = View.GONE
    }

    private fun showError(
        title: String, it: String
    ) {
        MyLayoutInflater().onAddField(
            requireContext(),
            binding.root,
            R.layout.layout_error,
            Constants.ERROR_TEXT_DRAWABLE,
            title,
            it
        )

    }

    private fun hideError() {
        if (binding.root.findViewById<LinearLayout>(R.id.layout_root) != null) {
            MyLayoutInflater().onDelete(
                binding.root,
                binding.root.findViewById(R.id.layout_root)
            )
        }
    }

    private fun prepareOrder(detail: List<Detail>) {
        Log.d("testmyorder", "prepareOrder: " + detail.size);
        val array = ArrayList<MyOrderRecyclerViewItem>()
        detail.forEach { d ->
            array.add(MyOrderRecyclerViewItem.Title(d.orderDate, "Order ID: "))
            d.orders.forEach { order ->
                array.add(
                    MyOrderRecyclerViewItem.Product(
                        order.products.name,
                        "${order.products.quantity}",
                        order.products.imageUrl,
                        order.nprTotal.toString(),
                        order.dollarTotal.toString(),
                        order,
                        order.currentStatus.contains("Order Cancelled", ignoreCase = true)
                    )
                )
            }
            array.add(MyOrderRecyclerViewItem.Divider())
        }
        myAdapter.items = array

    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            OrderViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[OrderViewModel::class.java]

    }

    private fun setUpRecyclerView() {
        binding.rvOrders.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = myAdapter
//            myAdapter.items = arrayListOf(
//                MyOrderRecyclerViewItem.Title("April 12, 2021", "Order ID: Q935-Z324"),
//                MyOrderRecyclerViewItem.Product("Nike Air Jordan", "Quantity: 1", "","7855","900"),
//                MyOrderRecyclerViewItem.Divider(),
//                MyOrderRecyclerViewItem.Title("April 12, 2021", "Order ID: Q935-Z324"),
//                MyOrderRecyclerViewItem.Product("Nike Air Jordan", "Quantity: 1", "","10000","800"),
//                MyOrderRecyclerViewItem.Product("Nike Air Jordan", "Quantity: 1", "","8900","600"),
//            )
        }
        myAdapter.setitemOrderClick {
//            if (!it.isCancelled) {
                val b = Bundle()
                b.putSerializable("order", it.order)
                val frag = FragmentOrderDetail()
                frag.arguments = b
                (activity as MainActivity).openOrderDetail(frag)
//            } else {
//                Toast.makeText(requireContext(), "Order was Cancelled", Toast.LENGTH_SHORT).show()
//            }
        }
    }
//    private fun groupDataIntoHashMap(chatModelList: List<ApiOrder>) {
//        val groupedHashMap: LinkedHashMap<String, MutableSet<ApiOrder>?> =
//            LinkedHashMap<String, MutableSet<ApiOrder>?>()
//        var list: MutableSet<ApiOrder>? = null
//        for (chatModel in chatModelList) {
//            //Log.d(TAG, travelActivityDTO.toString());
//            val hashMapKey: String = DateParser.convertDateToString(chatModel.order_date)
//            //Log.d(TAG, "start date: " + DateParser.convertDateToString(travelActivityDTO.getStartDate()));
//            if (groupedHashMap.containsKey(hashMapKey)) {
//                // The key is already in the HashMap; add the pojo object
//                // against the existing key.
//                groupedHashMap[hashMapKey]!!.add(chatModel)
//            } else {
//                // The key is not there in the HashMap; create a new key-value pair
//                list = LinkedHashSet<ApiOrder>()
//                list!!.add(chatModel)
//                groupedHashMap[hashMapKey] = list
//            }
//        }
//        //Generate list from map
//        generateListFromMap(groupedHashMap)
//    }


//    private fun generateListFromMap(groupedHashMap: LinkedHashMap<String, MutableSet<ApiOrder>?>): List<ListObject>? {
//        // We linearly add every item into the consolidatedList.
//        val consolidatedList: MutableList<ListObject> = ArrayList<ListObject>()
//        for (date in groupedHashMap.keys) {
//            val dateItem = DateObject()
//            dateItem.setDate(date)
//            consolidatedList.add(dateItem)
//            for (chatModel in groupedHashMap[date]!!) {
//                val generalItem = ChatModelObject()
//                generalItem.setChatModel(chatModel)
//                consolidatedList.add(generalItem)
//            }
//        }
//        chatAdapter.setDataChange(consolidatedList)
//        return consolidatedList
//    }
//    data class ApiOrder(val orderId:Int,val order_date:String,val productList:List<ModelProduct>)
}