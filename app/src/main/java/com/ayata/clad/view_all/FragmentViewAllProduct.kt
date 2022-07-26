package com.ayata.clad.view_all

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentViewAllProductBinding
import com.ayata.clad.databinding.LayoutErrorPagingBinding
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.ProductLoadStateAdapter
import com.ayata.clad.view_all.adapter.AdapterViewAllProduct2
import com.ayata.clad.view_all.paging.ProductDetailViewAllAdapter
import com.ayata.clad.view_all.viewmodel.ProductAllViewModel
import com.ayata.clad.view_all.viewmodel.ProductAllViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.util.*


class FragmentViewAllProduct : Fragment(), AdapterViewAllProduct2.OnItemClickListener,
    ProductDetailViewAllAdapter.onItemClickListener {

    companion object {
        private const val TAG = "FragmentViewAllProduct"
    }

    private lateinit var binding: FragmentViewAllProductBinding
    private lateinit var mergeBinding: LayoutErrorPagingBinding

    private var listItem = ArrayList<ProductDetail?>()
    private lateinit var adapterRecycler: AdapterViewAllProduct2
    private lateinit var adapterPaging: ProductDetailViewAllAdapter
    private lateinit var viewModel: ProductAllViewModel
    val QUERY_PAGE = 16
    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    var isFirstTime = true
    var currentPage = 1

    private var listImage = arrayListOf<String>(
        "https://freepngimg.com/thumb/categories/627.png",
        "https://images.squarespace-cdn.com/content/v1/566e100d0e4c116bdc11b2c2/1473302788755-FL48S6YFWHYC9KU18K52/245282-ceb4145ac7b646889a16b6f5dbd2f455.png?format=750w",
        "https://www.pngkit.com/png/full/70-704028_running-shoes-png-image-running-shoes-clipart-transparent.png"
    )

    var title: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        val bundle = arguments
        if (bundle != null) {
            title = bundle.getString(Constants.FILTER_HOME, "")
        }
        viewModel.searchProductViewAll(title, PreferenceHandler.getToken(context).toString())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewAllProductBinding.inflate(inflater, container, false)
        mergeBinding = LayoutErrorPagingBinding.bind(binding.root)
        initAppbar()
        initRecycler()
        listItem.clear()
        getAllTest()


        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            ProductAllViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[ProductAllViewModel::class.java]
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(
            title.replace("_"," "),
            isSearch = true,
            isProfile = false,
            isClose = false,
            isLogo = false,
        )
    }

    private fun initRecycler() {
//        binding.recyclerView.setHasFixedSize(true)
//        adapterRecycler = AdapterViewAllProduct2(context, listItem, this)
//        binding.recyclerView.apply {
//            layoutManager = GridLayoutManager(context, 2)
//            adapter = adapterRecycler
//        }
        adapterPaging = ProductDetailViewAllAdapter(this)
        binding.apply {
            recyclerView.apply {
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
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                mergeBinding.textViewError.isVisible = loadState.source.refresh is LoadState.Error
                mergeBinding.buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                //for empty view
                if (loadState.source.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached
                    && adapterPaging.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    mergeBinding.textViewEmpty.isVisible = true

                } else {
                    mergeBinding.textViewEmpty.isVisible = false
                }

            }
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
//
//    fun hideProgress() {
//        isLoading = false
//        if (isFirstTime) {
//            binding.defaultProgress.visibility = View.GONE
//        } else {
//            binding.loadMoreProgress.visibility = View.GONE
//        }
//    }

    private fun setUpEmptyView(message: String) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun getAllTest() {
        viewModel.productList.observe(viewLifecycleOwner, {
            adapterPaging.submitData(viewLifecycleOwner.lifecycle, it)
            adapterPaging.snapshot().items
        })
    }

    private fun removeWishListAPI(productDetail: ProductDetail, position: Int) {
        viewModel.removeFromWishAPI(
            PreferenceHandler.getToken(context).toString(),
            productDetail.productId
        )
        viewModel.getRemoveFromWishAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "removeWishListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            listItem.get(position)!!.isInWishlist = false;
                            adapterRecycler.notifyItemChanged(position)
                            showSnackBar(msg = "Product removed from wishlist")
                        } catch (e: Exception) {
                            Log.d(TAG, "removeWishListAPI:Error ${e.message}")
                        }
                    }

                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "removeWishListAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun addToWishListAPI(productDetail: ProductDetail, position: Int) {
        viewModel.addToWishAPI(
            PreferenceHandler.getToken(context).toString(),
            productDetail?.variants[0]?.variantId ?: 0
        )
        viewModel.getAddToWishAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "addToWishListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
//                        listItem.get(position)!!.isInWishlist = true;
//                        adapterRecycler.notifyItemChanged(position)
                            productDetail.isInWishlist = true
                            adapterPaging.notifyItemChanged(position)
                            showSnackBar(jsonObject.get("message").toString())
                            MainActivity.NavCount.myWishlist =
                                MainActivity.NavCount.myWishlist?.plus(1)

                        } catch (e: Exception) {
                            Log.d(TAG, "addToWishListAPI:Error ${e.message}")
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "addToWishListAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun showSnackBar(msg: String) {
        val snackBar = Snackbar
            .make(binding.root, msg, Snackbar.LENGTH_SHORT)
        snackBar.setActionTextColor(Color.WHITE)
        snackBar.show()
    }

    override fun onProductClickListener(data: ProductDetail) {

        Log.d("testmyfilter", "setUpRecyclerRecommendation: $data")
        val bundle = Bundle()
        bundle.putSerializable(FragmentHome.PRODUCT_DETAIL, data)
        val fragmentProductDetail = FragmentProductDetail()
        fragmentProductDetail.arguments = bundle
        parentFragmentManager.beginTransaction().replace(
            R.id.main_fragment,
            fragmentProductDetail
        ).addToBackStack(null).commit()
    }

    override fun onWishListClicked(data: ProductDetail, position: Int) {
        val isOnWishList = listItem[position]!!.isInWishlist
//        api call
        if (isOnWishList) {
//            removeWishListAPI(data, position)
        } else {

            if (PreferenceHandler.getToken(context) != "")
                addToWishListAPI(data, position) else (activity as MainActivity).showDialogLogin()
        }
    }

    override fun onItemClick(data: ProductDetail, position: Int) {
        Log.d("testmyfilter", "setUpRecyclerRecommendation: $data")
        val bundle = Bundle()
        bundle.putSerializable(FragmentHome.PRODUCT_DETAIL, data)
        val fragmentProductDetail = FragmentProductDetail()
        fragmentProductDetail.arguments = bundle
        parentFragmentManager.beginTransaction().replace(
            R.id.main_fragment,
            fragmentProductDetail
        ).addToBackStack(null).commit()
    }

    override fun onWishListClick(data: ProductDetail, position: Int) {
        val isOnWishList = data.isInWishlist
        Log.d(TAG, "onWishListClick: $isOnWishList")
//        api call
        if (isOnWishList) {
//            removeWishListAPI(data, position)
        } else {

            if (PreferenceHandler.getToken(context) != "")
                addToWishListAPI(data, position) else (activity as MainActivity).showDialogLogin()
        }
    }


}