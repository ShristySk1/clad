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
import android.widget.LinearLayout
import androidx.annotation.DimenRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentProductListBinding
import com.ayata.clad.databinding.LayoutErrorPagingBinding
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.productlist.adapter.AdapterProductList
import com.ayata.clad.productlist.viewmodel.ProductListViewModel
import com.ayata.clad.productlist.viewmodel.ProductListViewModelFactory
import com.ayata.clad.profile.account.AccountViewModel
import com.ayata.clad.shop.FragmentSubCategory
import com.ayata.clad.shop.response.ChildCategory
import com.ayata.clad.utils.*
import com.ayata.clad.view_all.paging.ProductDetailViewAllAdapter


class FragmentProductList : Fragment(), ProductDetailViewAllAdapter.onItemClickListener {
    private val TAG: String = "FragmentProductList"
    private lateinit var binding: FragmentProductListBinding
    private lateinit var mergeBinding: LayoutErrorPagingBinding

    private lateinit var adapterProductList: AdapterProductList
    private var listProduct = ArrayList<ProductDetail>()
    private val adapterPaging by lazy { ProductDetailViewAllAdapter(this) }

    private var appBarTitle: String = "";
    private var appBarDesc: String = "";
    private var appBarCount: Int = 0

    //paging
    var isLastPage = false
    var isScrolling = false
    var isFirstTime = true

    private lateinit var viewModel: ProductListViewModel
    private lateinit var testViewModel: AccountViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductListBinding.inflate(inflater, container, false)
        mergeBinding = LayoutErrorPagingBinding.bind(binding.root)
        getAllTest()
        setUpRecyclerProductList()
        initAppbar()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        getBundle()
    }

    private fun getBundle() {
        if (arguments != null) {
            val child =
                requireArguments().getSerializable(FragmentSubCategory.CHILD_CATEGORY) as ChildCategory
            appBarTitle = child.title
            appBarCount = child.product_count
            appBarDesc = requireArguments().getString(FragmentSubCategory.CATEGORY_TITLE, "")
//            getCategoryProductListAPI(child.id,true)
            viewModel.searchProductListFromCategory(
                PreferenceHandler.getToken(requireContext())!!,
                child.slug, "", "", "", "", "", ""
            )
            (activity as MainActivity).filterSlugCategory = child.slug
        }
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            ProductListViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[ProductListViewModel::class.java]
        testViewModel = ViewModelProviders.of(requireActivity()).get(AccountViewModel::class.java)
        testViewModel.clear()
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = true, isClear = false,
            textTitle = appBarDesc + "." + appBarTitle,
            textDescription = "$appBarCount results"
        )
    }
    private fun setUpRecyclerProductList() {
        binding.apply {
            rvProductList.apply {
                layoutManager = GridLayoutManager(context, 2)

                itemAnimator = null
                setHasFixedSize(true)
//                adapter = unsplashAdapter
                adapter = adapterPaging.withLoadStateHeaderAndFooter(
                    header = ProductLoadStateAdapter {
                        adapterPaging.retry()
                    },
                    footer = ProductLoadStateAdapter {
                        adapterPaging.retry()
                    }
                )
            }
            mergeBinding.buttonRetry.setOnClickListener {
                adapterPaging.retry()
            }
        }
        //load state
        adapterPaging.addLoadStateListener { loadState ->
            binding.apply {
                mergeBinding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                rvProductList.isVisible = loadState.source.refresh is LoadState.NotLoading
                mergeBinding.textViewError.isVisible = loadState.source.refresh is LoadState.Error
                mergeBinding.buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                //for empty view
                if (loadState.source.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached
                    && adapterPaging.itemCount < 1
                ) {
                    rvProductList.isVisible = false
                    mergeBinding.textViewEmpty.isVisible = true

                } else {
                    mergeBinding.textViewEmpty.isVisible = false
                }

            }
        }
    }

    private fun getAllTest() {
        testViewModel.getData()?.let {
            viewModel.searchProductListFromCategory(
                PreferenceHandler.getToken(requireContext())!!,
                it.filterSlug,
                it.refactorAllFromList,
                it.refactorAllFromList1,
                it.refactorAllFromList2,
                it.mysortByFilter,
                it.newMin,
                it.newMax
            )
        }
        Log.d(TAG, "getAllTest: " + testViewModel.getData())
        viewModel.productList?.observe(viewLifecycleOwner, {
            viewModel.getCurrent()
            adapterPaging.submitData(this.lifecycle, it)
            it.map { Log.d(TAG, "getAllTest: " + it.name) }

        })
    }


    private fun removeEmptyView() {
        if (binding.root.findViewById<LinearLayout>(R.id.layout_root) != null) {
            MyLayoutInflater().onDelete(
                binding.root,
                binding.root.findViewById(R.id.layout_root)
            )
        }
    }
    override fun onItemClick(product: ProductDetail, position: Int) {
        Log.d("testmyfilter", "setUpRecyclerRecommendation: $product")
        val bundle = Bundle()
        bundle.putSerializable(FragmentHome.PRODUCT_DETAIL, product)
        val fragmentProductDetail = FragmentProductDetail()
        fragmentProductDetail.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, fragmentProductDetail)
            .addToBackStack(null).commit()
    }

    override fun onWishListClick(product: ProductDetail, position: Int) {

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