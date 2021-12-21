package com.ayata.clad.profile.myorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.databinding.FragmentMyorderBinding
import com.ayata.clad.profile.MyOrderRecyclerViewItem

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
        val myAdapter = AdapterOrders(listOf())
        binding.rvOrders.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = myAdapter
            myAdapter.items = arrayListOf(
                MyOrderRecyclerViewItem.Title("April 12, 2021", "Order ID: Q935-Z324"),
                MyOrderRecyclerViewItem.Product("Nike Air Jordan", "Quantity: 1", ""),
                MyOrderRecyclerViewItem.Divider(),
                MyOrderRecyclerViewItem.Title("April 12, 2021", "Order ID: Q935-Z324"),
                MyOrderRecyclerViewItem.Product("Nike Air Jordan", "Quantity: 1", ""),
                MyOrderRecyclerViewItem.Product("Nike Air Jordan", "Quantity: 1", ""),
            )
        }

    }

}