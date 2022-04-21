package com.ayata.clad.filter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.DialogFilterBinding
import com.ayata.clad.databinding.DialogFilterRangeBinding
import com.ayata.clad.databinding.FragmentFilterBinding
import com.ayata.clad.filter.filterdialog.AdapterFilterContent
import com.ayata.clad.filter.filterdialog.MyFilterContentViewItem
import com.ayata.clad.utils.removeBracket
import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentFilter : Fragment() {
    private lateinit var binding: FragmentFilterBinding
    lateinit var myAdapter: AdapterFilter
    lateinit var sizeList: List<MyFilterContentViewItem.MultipleChoice>
    lateinit var colorList: List<MyFilterContentViewItem.MultipleChoiceColor>
    lateinit var brandList: List<MyFilterContentViewItem.MultipleChoice>
    lateinit var sortList: List<MyFilterContentViewItem.SingleChoice>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        initAppbar()
        //set initial value
        sizeList = listOf(
            MyFilterContentViewItem.MultipleChoice("M", false),
            MyFilterContentViewItem.MultipleChoice("S", false),
            MyFilterContentViewItem.MultipleChoice("L", false),
            MyFilterContentViewItem.MultipleChoice("XL", false),
        )
        colorList = listOf(
            MyFilterContentViewItem.MultipleChoiceColor("Blue", false, "#ffffff"),
            MyFilterContentViewItem.MultipleChoiceColor("Black", false, "#ffffff"),
            MyFilterContentViewItem.MultipleChoiceColor("Red", false, "#ffffff"),
            MyFilterContentViewItem.MultipleChoiceColor("Yellow", false, "#ffffff"),
        )
        sortList= listOf(
            MyFilterContentViewItem.SingleChoice("Most Popular", false),
            MyFilterContentViewItem.SingleChoice("Cheapest (low - high)", false),
            MyFilterContentViewItem.SingleChoice("Most Expensive (high - low)", false),
            MyFilterContentViewItem.SingleChoice("Latest", false),
        )
        setUpRecyclerView()

        return binding.root
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = true, isBack = false, isFilter = false, isClear = true,
            textTitle = getString(R.string.refine_filter),
            textDescription = "1200 results"
        )
    }

    private fun setUpRecyclerView() {
        myAdapter = AdapterFilter(listOf())
        binding.rvColors.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = myAdapter
            myAdapter.items =
                arrayListOf(
                    MyFilterRecyclerViewItem.Title(1, "Sort by", "Recommended"),
//                    MyFilterRecyclerViewItem.Title(2, "Product Type", "Shoes / Sneakers"),
                    MyFilterRecyclerViewItem.Title(3, "Brand", "Adidas Originals / Nike / Vans"),
                    MyFilterRecyclerViewItem.Title(4, "Size", "US 10 / US 10.5 / US 11"),
                    MyFilterRecyclerViewItem.Color(
                        7, "Color", listOf(
                            MyColor("Black", "#000000"),
                            MyColor("Blue", "#ffffff"),
                            MyColor("Gray", "#ffffff")
                        )
                    ),
//                    MyFilterRecyclerViewItem.Title(5, "Discount", "10% and above"),
                    MyFilterRecyclerViewItem.Title(6, "Price Range", "Rs. 1000 - Rs. 25000")
                )
            myAdapter.setFilterClickListener { it, pos ->
                Log.d("testcolor", "setUpRecyclerView: " + it.id);
                when (it.id) {
                    1 -> {//Sort by
                        showDialogSingleChoice(it.title, sortList)
                    }
                    2 -> {//Product Type

                    }
                    3 -> {//Brand
                    }
                    4 -> {//Size
                        showDialogMultipleChoice(it.title, sizeList, pos)
                    }
                    5 -> {//Discount

                    }
                    6 -> {//Price Range
                        showDialogRange(it.title)

                    }
                }
            }
            myAdapter.setFilterColorClickListener { it, pos ->

                showDialogMultipleChoiceColor(it.title, colorList, pos)
            }
        }
    }

    private fun showDialogSingleChoice(
        title: String,
        list: List<MyFilterContentViewItem.SingleChoice>
    ) {
        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)

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

        private fun showDialogMultipleChoiceColor(
        title: String,
        list: List<MyFilterContentViewItem.MultipleChoiceColor>, pos: Int
    ) {

        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        val adapterfilterContent = AdapterFilterContent(
            context, list
        ).also { adapter ->
            adapter.setFilterContentMultipleColorClickListener { data ->
                Log.d("testfilter", "showDialogMultipleChoice: " + data);
                for (item in list) {
                    if (item.equals(data)) {
                        item.isSelected = !item.isSelected
                        adapter.notifyItemChanged(list.indexOf(item))
                        break
                    }

                }
                (myAdapter.items.get(pos) as MyFilterRecyclerViewItem.Color).colorList =
                    list.filter { it.isSelected }
                        .map {
                            MyColor(it.title, it.colorHex!!)
                        }
                myAdapter.notifyItemChanged(pos)
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
    private fun showDialogMultipleChoice(
        title: String,
        list: List<MyFilterContentViewItem.MultipleChoice>, pos: Int
    ) {
        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        val adapterfilterContent = AdapterFilterContent(
            context, list
        ).also { adapter ->
            adapter.setFilterContentMultipleClickListener { data ->
                Log.d("testfilter", "showDialogMultipleChoice: " + data);
                for (item in list) {
                    if (item.equals(data)) {
                        item.isSelected = !item.isSelected
                        adapter.notifyItemChanged(list.indexOf(item))
                        break
                    }
                }
                (myAdapter.items.get(pos) as MyFilterRecyclerViewItem.Title).subTitle =
                    list.filter { it.isSelected }
                        .map { it.title }.toString().removeBracket()
                myAdapter.notifyItemChanged(pos)
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

    private fun showDialogRange(
        title: String
    ) {
        val dialogBinding = DialogFilterRangeBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        dialogBinding.title.text = title
        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }
}