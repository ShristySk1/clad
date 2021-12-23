package com.ayata.clad.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.DialogFilterBinding
import com.ayata.clad.databinding.FragmentWishlistBinding
import com.ayata.clad.filter.AdapterFilterContent
import com.ayata.clad.filter.ModelFilterContent
import com.ayata.clad.filter.MyFilterContentViewItem
import com.ayata.clad.product.AdapterRecommendation
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.product.ModelProduct
import com.ayata.clad.product.productlist.ItemOffsetDecoration
import com.google.android.material.bottomsheet.BottomSheetDialog


class FragmentWishlist : Fragment() {
    private lateinit var binding: FragmentWishlistBinding
    private lateinit var myProductList: List<ModelProduct>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWishlistBinding.inflate(inflater, container, false)
        initAppbar()
        settUpFilterListener()
        myProductList = listOf()
        setUpRecyclerProductList()
        if (myProductList.isEmpty()) {
            binding.rvWishList.visibility = View.GONE
            binding.llEmpty.visibility = View.VISIBLE
            setUpRecyclerRecommendation()
        } else {
            binding.llEmpty.visibility = View.GONE
            binding.rvWishList.visibility = View.VISIBLE
        }
        return binding.root
    }

    private fun settUpFilterListener() {
        binding.btnFilter.setOnClickListener {
            val list = listOf(
                MyFilterContentViewItem.SingleChoice("Recommended", true),
                MyFilterContentViewItem.SingleChoice("Price (low - high)", false),
                MyFilterContentViewItem.SingleChoice("Price (high - low)", false),
            )
            showDialog(
                "SORT BY", list
            )
        }
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(
            getString(R.string.wishlist),
            isSearch = false,
            isProfile = true,
            isClose = false
        )
    }

    private fun setUpRecyclerRecommendation() {
        binding.rvRecommendation.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = AdapterRecommendation(
                listOf(
                    R.color.color1,
                    R.color.color2,
                    R.color.color3
                )
            ).also {
                it.setProductClickListener { recommendedProduct ->
                    parentFragmentManager.beginTransaction().replace(
                        R.id.main_fragment,
                        FragmentProductDetail()
                    )
                        .addToBackStack(null).commit()
                }
            }
        }

    }

    private fun setUpRecyclerProductList() {
        myProductList = listOf(
            ModelProduct(1, "", "ss", "com", "$123", false),
            ModelProduct(2, "", "ss", "com", "$123", false),
            ModelProduct(3, "", "ss", "com", "$123", false),
            ModelProduct(1, "", "ss", "com", "$123", false)
        )
        binding.rvWishList.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2)
            adapter = AdapterWishList(
                myProductList
            ).also {
                it.setProductClickListener { recommendedProduct ->
                    //wishlist
                    parentFragmentManager.beginTransaction().replace(
                        R.id.main_fragment,
                        FragmentProductDetail()
                    )
                        .addToBackStack(null).commit()
                }
            }.also {
                it.setSettingClickListener {
                    val list = listOf(
                        MyFilterContentViewItem.SingleChoice("Add to Bag", false),
                        MyFilterContentViewItem.SingleChoice("Remove from wishlist", false)
                    )
                    showDialog("Options", list)
                }
            }
            val itemDecoration = ItemOffsetDecoration(context, R.dimen.item_offset)
            addItemDecoration(itemDecoration)
        }
    }

    private fun showDialog(title: String, listContent: List<MyFilterContentViewItem.SingleChoice>) {
        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        val list = listContent
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