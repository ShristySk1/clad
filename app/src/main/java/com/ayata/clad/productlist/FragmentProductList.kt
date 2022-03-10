package com.ayata.clad.productlist

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentProductListBinding
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.productlist.adapter.AdapterProductList
import com.ayata.clad.productlist.viewmodel.ProductListViewModel
import com.ayata.clad.productlist.viewmodel.ProductListViewModelFactory
import com.ayata.clad.shop.FragmentSubCategory
import com.ayata.clad.shop.response.ChildCategory
import com.ayata.clad.view_all.FragmentViewAllProduct
import com.google.gson.Gson


class FragmentProductList : Fragment() {
    private val TAG: String = "FragmentProductList"
    private lateinit var binding: FragmentProductListBinding
    private lateinit var adapterProductList: AdapterProductList
    private var listProduct = ArrayList<ProductDetail>()
    private var appBarTitle: String = "";
    private var appBarDesc: String = "";


    //paging
    val QUERY_PAGE = 16
    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    var isFirstTime = true

    private lateinit var viewModel: ProductListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductListBinding.inflate(inflater, container, false)
        setUpViewModel()
        setUpRecyclerProductList()
        getBundle()
        initAppbar()
        initSearchView()
        return binding.root
    }

    private fun getBundle() {
        if (arguments != null) {
            val child =
                requireArguments().getSerializable(FragmentSubCategory.CHILD_CATEGORY) as ChildCategory
            appBarTitle = child.title
            appBarDesc = requireArguments().getString(FragmentSubCategory.CATEGORY_TITLE, "")
            getCategoryProductListAPI(child.id,true)
        }
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            ProductListViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[ProductListViewModel::class.java]
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = true, isClear = false,
            textTitle = appBarDesc + "." + appBarTitle,
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
        adapterProductList = AdapterProductList(
            context,
            listProduct
        ).also {
            it.setProductClickListener { recommendedProduct ->
                Log.d("testmyfilter", "setUpRecyclerRecommendation: $recommendedProduct")
                val bundle=Bundle()
                bundle.putSerializable(FragmentHome.PRODUCT_DETAIL,recommendedProduct)
                val fragmentProductDetail=FragmentProductDetail()
                fragmentProductDetail.arguments=bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragmentProductDetail)
                    .addToBackStack(null).commit()
            }
        }

        binding.rvProductList.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2)
            adapter = adapterProductList
//            val itemDecoration = ItemOffsetDecoration(context, R.dimen.item_offset)
//            addItemDecoration(itemDecoration)
            initScrollListener()

    }
    }
    private fun initScrollListener() {
        binding.rvProductList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d(TAG, "onScrolled: ");
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val isNotLoadingAndIsNoLastPage = !isLoading && !isLastPage
                val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                val isNotAtBeginning = firstVisibleItemPosition >= 0
                val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE
                val shouldPaginate =
                    isNotLoadingAndIsNoLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
                if (shouldPaginate) {
                    getCategoryProductListAPI(16,false)
                    isScrolling = false
                }
            }
        })
    }


    private fun prepareProductList(productDetail: ArrayList<ProductDetail>) {
//        listProduct.clear()
//        listProduct.addAll(listOf(
//            ModelProduct(1, "https://freepngimg.com/thumb/categories/627.png",
//                "Nike Air Max", "Nike Company", "123","50", false),
//            ModelProduct(2, "https://freepngimg.com/thumb/categories/627.png",
//                "Adidas Yeezy Boost 700 MNVN Bone", "Adidas Company", "123","54", false),
//            ModelProduct(3, "https://freepngimg.com/thumb/categories/627.png",
//                "Nike Air Max", "Nike Company", "123","50", false),
//            ModelProduct(4, "https://freepngimg.com/thumb/categories/627.png",
//                "Adidas Yeezy Boost 700 MNVN Bone", "Adidas Company", "123","50", false),
//            ModelProduct(5, "https://freepngimg.com/thumb/categories/627.png",
//                "Nike Air Max", "Nike Company", "123","50", false),
//            ModelProduct(6, "https://freepngimg.com/thumb/categories/627.png",
//                "Adidas Yeezy Boost 700 MNVN Bone", "Adidas Company", "123","50", false)
//
//        ))
        listProduct.clear()
        listProduct.addAll(productDetail)
        adapterProductList.notifyDataSetChanged()

    }

    private fun getCategoryProductListAPI(categoryId:Int,isfirst:Boolean) {
//        setShimmerLayout(true)
        isFirstTime=isfirst
        viewModel.categoryProductListAPI(categoryId)
        viewModel.getCategoryProductListAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
//                    setShimmerLayout(false)
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val message = jsonObject.get("message").asString
                            if (message.contains("empty.", true)) {
                                setUpEmptyView()
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "getproductlistAPI:Error ${e.message}")
                            try {
                                val productList = Gson().fromJson(
                                    jsonObject.get("products"),
                                    Array<ProductDetail>::class.java
                                ).asList()
                                if (productList != null) {
                                    if (productList.size > 0) {
                                        prepareProductList(ArrayList(productList))
                                    } else {
                                        setUpEmptyView()
                                    }

                                }
                            } catch (e: Exception) {
                                Log.d(TAG, "getproductlistAPI:Error ${e.message}")
                            }
                        }
                    }
                    hideProgress()
                }
                Status.LOADING -> {
                    showProgress()
                }
                Status.ERROR -> {
                    //Handle Error
//                    setShimmerLayout(false)
//                    listCheckout.clear()
//                    setUpView()
                    hideProgress()
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "getproductlistAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun setUpEmptyView() {
        Toast.makeText(context,"Empty products",Toast.LENGTH_SHORT).show()
    }
    fun showProgress() {
        isLoading = true
        if (isFirstTime) {
            binding.defaultProgress.visibility = View.VISIBLE
        } else {
            binding.loadMoreProgress.visibility = View.VISIBLE
        }
    }
    fun hideProgress() {
        isLoading = false
        if (isFirstTime) {
            binding.defaultProgress.visibility = View.GONE
        } else {
            binding.loadMoreProgress.visibility = View.GONE
        }
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
//        outRect[mItemOffset, mItemOffset, mItemOffset] = mItemOffset
        outRect.set(mItemOffset, 0, mItemOffset, 0)
    }
}