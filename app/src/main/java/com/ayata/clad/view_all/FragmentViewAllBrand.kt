package com.ayata.clad.view_all

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.brand.BrandDetailActivity
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentViewAllBrandBinding
import com.ayata.clad.databinding.LayoutErrorPagingBinding
import com.ayata.clad.home.response.Brand
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.ProductLoadStateAdapter
import com.ayata.clad.view_all.paging.BrandViewAllAdapter
import com.ayata.clad.view_all.viewmodel.BrandAllViewModel
import com.ayata.clad.view_all.viewmodel.BrandAllViewModelFactory


class FragmentViewAllBrand : Fragment(), BrandViewAllAdapter.onItemClickListener {
    companion object {
        private const val TAG = "FragmentViewAllBrand"
    }

    private lateinit var binding: FragmentViewAllBrandBinding
    private lateinit var mergeBinding: LayoutErrorPagingBinding

    //    private lateinit var adapterViewAllBrand: AdapterViewAllBrand
    private var listBrand = ArrayList<Brand?>()
    private lateinit var viewModel: BrandAllViewModel
    private lateinit var adapterPaging: BrandViewAllAdapter

    //data
    val QUERY_PAGE = 2
    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    var isFirstTime = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewAllBrandBinding.inflate(inflater, container, false)
        mergeBinding = LayoutErrorPagingBinding.bind(binding.root)

        initAppbar()
        initViewModel()
        initRecycler()
        listBrand.clear()
        getAllTest("brand")
//        getBrandListAPI(true)
//
//        initScrollListener()
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            BrandAllViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[BrandAllViewModel::class.java]
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(
            "Brands",
            isSearch = false,
            isProfile = false,
            isClose = false,
            isLogo = false
        )
    }

    private var listDrawable = arrayListOf<Int>(
        R.drawable.brand_aamayra,
        R.drawable.brand_aroan, R.drawable.brand_bishrom,
        R.drawable.brand_caliber, R.drawable.brand_creative_touch,
        R.drawable.brand_fibro, R.drawable.brand_fuloo,
        R.drawable.brand_gofi, R.drawable.brand_goldstar,
        R.drawable.brand_hillsandclouds, R.drawable.brand_jujuwears,
        R.drawable.brand_kasa, R.drawable.brand_ktm_city, R.drawable.brand_logo,
        R.drawable.brand_mode23, R.drawable.brand_newmew,
        R.drawable.brand_phalanoluga, R.drawable.brand_sabah,
        R.drawable.brand_station, R.drawable.brand_tsarmoire
    )

    private fun initRecycler() {
        adapterPaging = BrandViewAllAdapter(this)
        binding.apply {
            recyclerView.apply {
                layoutManager = GridLayoutManager(context, 2)

                itemAnimator = null
                setHasFixedSize(true)
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

    private fun getAllTest(filter: String) {
        viewModel.searchBrandViewAll(filter, PreferenceHandler.getToken(context).toString())
        viewModel.brandList.observe(viewLifecycleOwner, {
            adapterPaging.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

//    private fun initScrollListener() {
//        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                    isScrolling = true
//                }
//            }
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                Log.d(TAG, "onScrolled: ");
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//                val visibleItemCount = layoutManager.childCount
//                val totalItemCount = layoutManager.itemCount
//                val isNotLoadingAndIsNoLastPage = !isLoading && !isLastPage
//                val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
//                val isNotAtBeginning = firstVisibleItemPosition >= 0
//                val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE
//                val shouldPaginate =
//                    isNotLoadingAndIsNoLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
//                if (shouldPaginate) {
//                    getBrandListAPI(false)
//                    isScrolling = false
//                }
//            }
//        })
//    }

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

    private fun setUpEmptyView(message: String) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

//    override fun onBrandClickListener(data: Brand) {
////        val bundle=Bundle()
////        bundle.putString(Constants.FILTER_HOME,data.name)
////        val fragmentViewAllProduct=FragmentViewAllProduct()
////        fragmentViewAllProduct.arguments=bundle
////        parentFragmentManager.beginTransaction().replace(R.id.main_fragment,fragmentViewAllProduct)
////            .addToBackStack(null).commit()
//    }

//    private fun getBrandListAPI(boolean: Boolean) {
//        isFirstTime = boolean
//        viewModel.brandListApi("Brand",PreferenceHandler.getToken(requireContext())!!)
//        viewModel.getBrandListAPI().observe(viewLifecycleOwner, {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    Log.d(TAG, "getProductListAPI: ${it.data}")
//                    val jsonObject = it.data
//                    if (jsonObject != null) {
//                        try {
//                            //object
//                            try {
//                                val message = jsonObject.get("message").asString
//                                if (message.contains("Your product is empty.", true)) {
//                                    setUpEmptyView(message)
//                                }
//                            } catch (e: Exception) {
//                                Log.d(TAG, "getWishListAPI:Error ${e.message}")
//                                try {
//                                    val gson = Gson()
//                                    val type: Type =
//                                        object : TypeToken<List<Brand?>?>() {}.type
//                                    val brandList: List<Brand> =
//                                        gson.fromJson(jsonObject.get("brands"), type)
//                                    if (brandList != null) {
//                                        if (brandList.size > 0) {
//                                            val oldCount = listBrand.size
//                                            binding.recyclerView.post {
//                                                listBrand.addAll(brandList)
//                                                adapterViewAllBrand.updateList(
//                                                    listBrand,
//                                                    oldCount,
//                                                    listBrand.size
//                                                )
//                                            }
//
//                                        } else {
//                                            setUpEmptyView("empty product")
//                                        }
//
//                                    }
//                                } catch (e: Exception) {
//                                    setUpEmptyView(e.message.toString())
//
//                                }
//                            }
//                        } catch (e: Exception) {
//                            Log.d(TAG, "getProductListAPI:Error ${e.message}")
//                        }
//                    }
//                    hideProgress()
//                }
//                Status.LOADING -> {
//                    showProgress()
//                }
//                Status.ERROR -> {
//                    //Handle Error
//                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
//                    hideProgress()
//                }
//            }
//
//        })
//    }

    override fun onBrandClick(product: Brand?, position: Int) {
        val intent = Intent(activity, BrandDetailActivity::class.java)
        intent.putExtra("slug", product?.slug)
        startActivity(intent)
    }

}