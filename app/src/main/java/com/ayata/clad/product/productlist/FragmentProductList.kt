package com.ayata.clad.product.productlist

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentProductListBinding
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.product.ModelProduct


class FragmentProductList : Fragment() {
    private lateinit var binding: FragmentProductListBinding
    private lateinit var adapterProductList: AdapterProductList
    private var listProduct = ArrayList<ModelProduct>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductListBinding.inflate(inflater, container, false)
        initAppbar()
        setUpRecyclerProductList()
        initSearchView()
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = true, isClear = false,
            textTitle = "Men.shoes",
            textDescription = "1000 results"
        )
    }

    private fun initSearchView() {

        binding.searchText.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapterProductList.filter.filter(p0)
            }

            override fun afterTextChanged(p0: Editable?) {
                adapterProductList.filter.filter(p0)
            }
        })

    }

    private fun setUpRecyclerProductList() {
        adapterProductList = AdapterProductList(context,
            listProduct
        ).also {
            it.setProductClickListener { recommendedProduct ->
                Log.d("testmyfilter", "setUpRecyclerRecommendation: $recommendedProduct")
                parentFragmentManager.beginTransaction().replace(R.id.main_fragment,FragmentProductDetail())
                    .addToBackStack(null).commit()
            }
        }

        binding.rvProductList.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2)
            adapter = adapterProductList
            val itemDecoration = ItemOffsetDecoration(context, R.dimen.item_offset)
            addItemDecoration(itemDecoration)
        }

        prepareProductList()
    }

    private fun prepareProductList(){

        listProduct.clear()
        listProduct.addAll(listOf(
            ModelProduct(1, "https://freepngimg.com/thumb/categories/627.png",
                "Nike Air Max", "Nike Company", "Rs. 123", false),
            ModelProduct(2, "https://freepngimg.com/thumb/categories/627.png",
                "Adidas Yeezy Boost 700 MNVN Bone", "Adidas Company", "Rs. 123", false),
            ModelProduct(3, "https://freepngimg.com/thumb/categories/627.png",
                "Nike Air Max", "Nike Company", "Rs .123", false),
            ModelProduct(4, "https://freepngimg.com/thumb/categories/627.png",
                "Adidas Yeezy Boost 700 MNVN Bone", "Adidas Company", "Rs. 123", false),
            ModelProduct(5, "https://freepngimg.com/thumb/categories/627.png",
                "Nike Air Max", "Nike Company", "Rs. 123", false),
            ModelProduct(6, "https://freepngimg.com/thumb/categories/627.png",
                "Adidas Yeezy Boost 700 MNVN Bone", "Adidas Company", "Rs. 123", false)

        ))

        adapterProductList.setData(listProduct)
        adapterProductList.notifyDataSetChanged()
    }
}

class ItemOffsetDecoration(private val mItemOffset: Int) : ItemDecoration() {
    constructor(context: Context, @DimenRes itemOffsetId: Int) : this(
        context.resources.getDimensionPixelSize(itemOffsetId)
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