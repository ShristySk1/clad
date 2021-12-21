package com.ayata.clad.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.DialogFilterBinding
import com.ayata.clad.databinding.FragmentFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentFilter : Fragment() {
    private lateinit var binding: FragmentFilterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        initAppbar()
        setUpRecyclerView()
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = true, isBack = false, isFilter = false, isClear = true,
            textTitle = getString(R.string.refine_filter),
            textDescription = "1200 results"
        )
    }

    private fun setUpRecyclerView() {
        val myAdapter = AdapterFilter(listOf())
        binding.rvColors.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = myAdapter
            myAdapter.items =
                arrayListOf(
                    MyFilterRecyclerViewItem.Title(1, "Sort by", "Recommended"),
                    MyFilterRecyclerViewItem.Title(2, "Product Type", "Shoes / Sneakers"),
                    MyFilterRecyclerViewItem.Title(3, "Brand", "Adidas Originals / Nike / Vans"),
                    MyFilterRecyclerViewItem.Title(4, "Size", "US 10 / US 10.5 / US 11"),
                    MyFilterRecyclerViewItem.Title(5, "Discount", "10% and above"),
                    MyFilterRecyclerViewItem.Title(6, "Price Range", "Rs. 1000 - Rs. 25000")
                )
            myAdapter.setFilterClickListener {
                when (it.id) {
                    1 -> {//Sort by
                        showDialog(it.title)
                    }
                    2 -> {//Product Type

                    }
                    3 -> {//Brand
                    }
                    4 -> {//Size

                    }
                    5 -> {//Discount

                    }
                    6 -> {//Price Range

                    }
                }
            }
        }
    }

    private fun showDialog(title: String) {
        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        val list = listOf(
            ModelFilterContent("Recommended", true),
            ModelFilterContent("Price (low - high)", false),
            ModelFilterContent("Price (high - low)", false)
        )
        val adapterfilterContent = AdapterFilterContent(
            context, list
        ).also { adapter ->
            adapter.setCircleClickListener { data ->
                for (item in list) {
                    item.isSelected = item.equals(data)
                }
                adapter.notifyDataSetChanged()
            }
        }
        dialogBinding.title.text = title
        dialogBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterfilterContent
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }
}