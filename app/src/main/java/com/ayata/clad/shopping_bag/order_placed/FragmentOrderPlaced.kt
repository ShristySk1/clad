package com.ayata.clad.shopping_bag.order_placed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentOrderPlacedBinding
import com.ayata.clad.profile.FragmentProfile
import com.ayata.clad.profile.myorder.order.FragmentOrderDetail
import com.ayata.clad.profile.myorder.order.response.Order


class FragmentOrderPlaced : Fragment() {

    private lateinit var binding: FragmentOrderPlacedBinding
    lateinit var order: ArrayList<Order>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderPlacedBinding.inflate(inflater, container, false)
        initAppbar()
        initView()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBundle()
    }

    fun getBundle() {
        arguments?.let {
            order = it.getSerializable("order") as ArrayList<Order>
        }
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(false)
    }

    private fun initView() {
        binding.textDate.text = ""
        binding.btnViewOrder.text = "Shop More"
        binding.btnViewOrder.setOnClickListener {
//            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            (activity as MainActivity).openHomePage()

        }
        binding.btnView.setOnClickListener {
            val b = Bundle()
            b.putBoolean("showOrder", true)
            val frag = FragmentProfile()
            frag.arguments = b
            (activity as MainActivity).openOrderList(frag)
        }
    }
}