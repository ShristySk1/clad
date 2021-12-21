package com.ayata.clad.product.productlist

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentProductListBinding
import com.ayata.clad.product.ModelProduct


class FragmentProductList : Fragment() {
    private lateinit var binding: FragmentProductListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductListBinding.inflate(inflater, container, false)
        setUpRecyclerProductList()
        return binding.root
    }

    private fun setUpRecyclerProductList() {
        binding.rvProductList.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2)
            adapter = AdapterProductList(
                listOf(
                    ModelProduct(1, "", "ss", "com", "$123", false),
                    ModelProduct(2, "", "ss", "com", "$123", false),
                    ModelProduct(3, "", "ss", "com", "$123", false),
                    ModelProduct(1, "", "ss", "com", "$123", false),
                    ModelProduct(2, "", "ss", "com", "$123", false),
                    ModelProduct(3, "", "ss", "com", "$123", false)

                )
            ).also {
                it.setProductClickListener { recommendedProduct ->
                    Log.d("testmyfilter", "setUpRecyclerRecommendation: " + recommendedProduct);
                }
            }
            val itemDecoration = ItemOffsetDecoration(context, R.dimen.item_offset)
            addItemDecoration(itemDecoration)
        }
    }
}
class ItemOffsetDecoration(private val mItemOffset: Int) : ItemDecoration() {
    constructor(context: Context, @DimenRes itemOffsetId: Int) : this(
        context.getResources().getDimensionPixelSize(itemOffsetId)
    ) {
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect[mItemOffset, mItemOffset, mItemOffset] = mItemOffset
    }
}