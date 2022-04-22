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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.network.interceptor.EmptyException
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentProductListBinding
import com.ayata.clad.databinding.LayoutErrorPagingBinding
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.productlist.adapter.AdapterProductList
import com.ayata.clad.productlist.viewmodel.ProductListViewModel
import com.ayata.clad.productlist.viewmodel.ProductListViewModelFactory
import com.ayata.clad.shop.FragmentSubCategory
import com.ayata.clad.shop.response.ChildCategory
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.MyLayoutInflater
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.ProductLoadStateAdapter
import com.ayata.clad.view_all.paging.ProductDetailViewAllAdapter
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException


class FragmentProductList : Fragment(), ProductDetailViewAllAdapter.onItemClickListener {
    private val TAG: String = "FragmentProductList"
    private lateinit var binding: FragmentProductListBinding
    private lateinit var mergeBinding: LayoutErrorPagingBinding

    private lateinit var adapterProductList: AdapterProductList
    private var listProduct = ArrayList<ProductDetail>()
    private val adapterPaging by lazy {  ProductDetailViewAllAdapter(this)}

    private var appBarTitle: String = "";
    private var appBarDesc: String = "";
    private var appBarCount:Int=0

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
        mergeBinding = LayoutErrorPagingBinding.bind(binding.root)
        getAllTest()
        setUpRecyclerProductList()
        initAppbar()
        initSearchView()
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
            appBarCount=child.product_count
            appBarDesc = requireArguments().getString(FragmentSubCategory.CATEGORY_TITLE, "")
//            getCategoryProductListAPI(child.id,true)
            viewModel.searchProductListFromCategory(child.slug)
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
            textDescription = "$appBarCount results"
        )
    }

    private fun initSearchView() {

        binding.searchText.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                adapterProductList?.let {  adapterProductList.filter.filter(p0) }

            }

            override fun afterTextChanged(p0: Editable?) {
//                adapterProductList?.let {  it.filter.filter(p0)}
            }
        })

    }

    private fun setUpRecyclerProductList() {
//        adapterProductList = AdapterProductList(
//            context,
//            listProduct
//        ).also {
//            it.setProductClickListener { recommendedProduct ->
//                Log.d("testmyfilter", "setUpRecyclerRecommendation: $recommendedProduct")
//                val bundle=Bundle()
//                bundle.putSerializable(FragmentHome.PRODUCT_DETAIL,recommendedProduct)
//                val fragmentProductDetail=FragmentProductDetail()
//                fragmentProductDetail.arguments=bundle
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.main_fragment, fragmentProductDetail)
//                    .addToBackStack(null).commit()
//            }
//        }
//
//        binding.rvProductList.apply {
//            layoutManager =
//                GridLayoutManager(requireContext(), 2)
//            adapter = adapterProductList
////            val itemDecoration = ItemOffsetDecoration(context, R.dimen.item_offset)
////            addItemDecoration(itemDecoration)
//            initScrollListener()
//
//    }


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
//                defaultProgress.isVisible = loadState.source.refresh is LoadState.Loading
//                rvProductList.isVisible = loadState.source.refresh is LoadState.NotLoading
////                textViewError.isVisible = loadState.source.refresh is LoadState.Error
////                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
//                //for empty view
//                if (loadState.source.refresh is LoadState.NotLoading
//                    && loadState.append.endOfPaginationReached
//                    && adapterPaging.itemCount < 1
//                ) {
//                    rvProductList.isVisible = false
////                    textViewEmpty.isVisible = true
//
//                } else {
////                    textViewEmpty.isVisible = false
//                }
//                //handle view according to error
//                val errorState = when {
//                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
//                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
//                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
//                    else -> null
//                }
//
//                when (val throwable = errorState?.error) {
//                    is EmptyException -> { setUpEmptyView() }
//                    is HttpException -> {
//                        if (throwable.code() == 401) { /* Handle HTTP 401 */ }
//                        else { /* Do something else */ }
//                    }
//                    is IOException->{/* Handle IO exceptions */}
//                }
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
        viewModel.productList.observe(viewLifecycleOwner, {
            adapterPaging.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }
    private fun setUpEmptyView() {
//        Toast.makeText(context,"Empty products",Toast.LENGTH_SHORT).show()
        MyLayoutInflater().onAddField(requireContext(), binding.root, R.layout.layout_error,Constants.ERROR_TEXT_DRAWABLE,"Empty!","No products available")

    }
    private fun removeEmptyView(){
        if (binding.root.findViewById<LinearLayout>(R.id.layout_root) != null) {
            MyLayoutInflater().onDelete(
                binding.root,
                binding.root.findViewById(R.id.layout_root)
            )
        }
    }
//    fun showProgress() {
//        isLoading = true
//        if (isFirstTime) {
//            binding.defaultProgress.visibility = View.VISIBLE
//        } else {
//            binding.loadMoreProgress.visibility = View.VISIBLE
//        }
//    }
//    fun hideProgress() {
//        isLoading = false
//        if (isFirstTime) {
//            binding.defaultProgress.visibility = View.GONE
//        } else {
//            binding.loadMoreProgress.visibility = View.GONE
//        }
//    }

    override fun onItemClick(product: ProductDetail, position: Int) {
                        Log.d("testmyfilter", "setUpRecyclerRecommendation: $product")
                val bundle=Bundle()
                bundle.putSerializable(FragmentHome.PRODUCT_DETAIL,product)
                val fragmentProductDetail=FragmentProductDetail()
                fragmentProductDetail.arguments=bundle
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