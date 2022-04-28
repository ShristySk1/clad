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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentMyorderBinding
import com.ayata.clad.profile.MyOrderRecyclerViewItem
import com.ayata.clad.profile.myorder.order.FragmentOrderDetail
import com.ayata.clad.profile.myorder.order.cancel.CancelViewModel
import com.ayata.clad.profile.myorder.order.response.Detail
import com.ayata.clad.profile.myorder.order.response.OrderResponse
import com.ayata.clad.profile.myorder.viewmodel.OrderViewModel
import com.ayata.clad.profile.myorder.viewmodel.OrderViewModelFactory
import com.ayata.clad.utils.Caller
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.MyLayoutInflater
import com.ayata.clad.utils.PreferenceHandler
import com.google.gson.Gson


class FragmentMyOrder : Fragment() {
    lateinit var binding: FragmentMyorderBinding
    private lateinit var viewModel: OrderViewModel
    private lateinit var cancelViewModel: CancelViewModel
    val myAdapter by lazy { AdapterOrders(requireContext(), listOf()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("testcreate", "onCreateView: here oncreate 1");
        // Inflate the layout for this fragment
        binding =
            FragmentMyorderBinding.inflate(inflater, container, false)
        initRefreshLayout()
        setUpRecyclerView()
        getOrderApi()
        return binding.root
    }

    private fun initRefreshLayout() {
        //refresh layout on swipe
        binding.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getOrderApi()

        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    private fun getOrderApi() {
        viewModel.getOrderApi(PreferenceHandler.getToken(context)!!)
        showProgress()
        hideError()
        viewModel.observeOrderResponse().observe(viewLifecycleOwner, {
            Log.d("testobserve", "getOrderApi: " + it.status + hashCode());
            when (it.status) {
                Status.SUCCESS -> {
                    binding.swipeRefreshLayout.isRefreshing = false
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
                                    prepareOrder(detail)
                                } else {
                                    showEmpty("Empty!", "No any order yet.")
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("", "prepareAPI: ${e.message}")
                        }
                    }

                }
                Status.LOADING -> {

//                    binding.spinKit.visibility = View.VISIBLE

                }
                Status.ERROR -> {
                    binding.swipeRefreshLayout.isRefreshing = false
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
    private fun showEmpty(
        title: String, it: String
    ) {
//        MyLayoutInflater().onAddField(
//            requireContext(),
//            binding.rootContainer,
//            R.layout.layout_error,
//            Constants.ERROR_TEXT_DRAWABLE,
//            title,
//            it
//        )
        Caller().empty(title,it,requireContext(),binding.rootContainer)

    }

    fun hideProgress() {
        binding.progressBar.rootContainer.visibility = View.GONE
    }

    private fun showError(
        title: String, it: String
    ) {
//        MyLayoutInflater().onAddField(
//            requireContext(),
//            binding.rootContainer,
//            R.layout.layout_error,
//            Constants.ERROR_TEXT_DRAWABLE,
//            title,
//            it
//        )
        Caller().error(title,it,requireContext(),binding.rootContainer)

    }

    private fun hideError() {
//        if (binding.root.findViewById<LinearLayout>(R.id.layout_root) != null) {
//            MyLayoutInflater().onDelete(
//                binding.rootContainer,
//                binding.root.findViewById(R.id.layout_root)
//            )
//        }

        Caller().hideErrorEmpty(binding.rootContainer)

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
        }
        myAdapter.setitemOrderClick {
//            if (!it.isCancelled) {
            val b = Bundle()
            b.putSerializable("order", it.order)
            val frag = FragmentOrderDetail()
            frag.arguments = b
            (activity as MainActivity).openOrderDetail(frag)

        }
    }

}