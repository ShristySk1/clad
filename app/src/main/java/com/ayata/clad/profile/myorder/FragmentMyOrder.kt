package com.ayata.clad.profile.myorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.databinding.FragmentMyorderBinding
import com.ayata.clad.product.ModelProduct
import com.ayata.clad.profile.MyOrderRecyclerViewItem
import java.util.*


class FragmentMyOrder : Fragment() {
    lateinit var binding: FragmentMyorderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentMyorderBinding.inflate(inflater, container, false)
        setUpRecyclerView()
        return binding.root
    }

    private fun setUpRecyclerView() {
        val myAdapter = AdapterOrders(requireContext(),listOf())
        binding.rvOrders.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = myAdapter
            myAdapter.items = arrayListOf(
                MyOrderRecyclerViewItem.Title("April 12, 2021", "Order ID: Q935-Z324"),
                MyOrderRecyclerViewItem.Product("Nike Air Jordan", "Quantity: 1", "","7855","900"),
                MyOrderRecyclerViewItem.Divider(),
                MyOrderRecyclerViewItem.Title("April 12, 2021", "Order ID: Q935-Z324"),
                MyOrderRecyclerViewItem.Product("Nike Air Jordan", "Quantity: 1", "","10000","800"),
                MyOrderRecyclerViewItem.Product("Nike Air Jordan", "Quantity: 1", "","8900","600"),
            )
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