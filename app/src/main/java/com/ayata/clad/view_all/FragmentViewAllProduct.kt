package com.ayata.clad.view_all

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentViewAllProductBinding
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.view_all.adapter.AdapterViewAllProduct2
import com.ayata.clad.view_all.viewmodel.ProductAllViewModel
import com.ayata.clad.view_all.viewmodel.ProductAllViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*


class FragmentViewAllProduct : Fragment(), AdapterViewAllProduct2.OnItemClickListener {

    companion object {
        private const val TAG = "FragmentViewAllProduct"
    }

    private lateinit var binding: FragmentViewAllProductBinding
    private var listItem = ArrayList<ProductDetail?>()
    private lateinit var adapterRecycler: AdapterViewAllProduct2
    private lateinit var viewModel: ProductAllViewModel
    val QUERY_PAGE = 16
    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    var isFirstTime = true

    private var listImage = arrayListOf<String>(
        "https://freepngimg.com/thumb/categories/627.png",
        "https://images.squarespace-cdn.com/content/v1/566e100d0e4c116bdc11b2c2/1473302788755-FL48S6YFWHYC9KU18K52/245282-ceb4145ac7b646889a16b6f5dbd2f455.png?format=750w",
        "https://www.pngkit.com/png/full/70-704028_running-shoes-png-image-running-shoes-clipart-transparent.png"
    )

    var title: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewAllProductBinding.inflate(inflater, container, false)

        val bundle = arguments
        if (bundle != null) {
            title = bundle.getString(Constants.FILTER_HOME, "")
        }
        initViewModel()
        initAppbar()
        initRecycler()
        listItem.clear()
        getProductListAPI(title, true)
        initScrollListener()


        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            ProductAllViewModelFactory(ApiRepository(ApiService.getInstance()))
        )[ProductAllViewModel::class.java]
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(
            title,
            isSearch = true,
            isProfile = false,
            isClose = false,
            isLogo = false
        )
    }

    private fun initRecycler() {
        binding.recyclerView.setHasFixedSize(true)
        adapterRecycler = AdapterViewAllProduct2(context, listItem, this)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = adapterRecycler
        }
    }


    private fun initScrollListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    getProductListAPI(title, false)
                    isScrolling = false
                }
            }
        })
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
    private fun setUpEmptyView(message: String) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    override fun onProductClickListener(data: ProductDetail) {
        parentFragmentManager.beginTransaction().replace(
            R.id.main_fragment,
            FragmentProductDetail()
        ).addToBackStack(null).commit()
    }
    override fun onWishListClicked(data: ProductDetail, position: Int) {
        val isOnWishList = listItem[position]!!.is_in_wishlist
//        api call
        if (isOnWishList) {
            removeWishListAPI(data, position)
        } else {
            addToWishListAPI(data, position)
        }

    }



    private fun getProductListAPI(filter: String, firsttime: Boolean) {
        isFirstTime = firsttime
        viewModel.productListApi(filter)
        viewModel.getProductListAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "getProductListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            //object
                            try {
                                val message = jsonObject.get("message").asString
                                if (message.contains("Your product is empty.", true)) {
                                    setUpEmptyView(message)
                                }
                            } catch (e: Exception) {
                                Log.d(TAG, "getWishListAPI:Error ${e.message}")
                                try {
                                    val gson = Gson()
                                    val type: Type =
                                        object : TypeToken<List<ProductDetail?>?>() {}.type
                                    val productList: List<ProductDetail> =
                                        gson.fromJson(jsonObject.get("products"), type)
                                    if (productList != null) {
                                        if (productList.size > 0) {
                                            val oldCount = listItem.size
                                            binding.recyclerView.post {
                                                listItem.addAll(productList)
                                                adapterRecycler.updateList(
                                                    listItem,
                                                    oldCount,
                                                    listItem.size
                                                )
                                            }

                                        } else {
                                            setUpEmptyView("empty product")
                                        }

                                    }
                                } catch (e: Exception) {
                                    setUpEmptyView(e.message.toString())

                                }
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "getProductListAPI:Error ${e.message}")
                        }
                    }
                    hideProgress()
                }
                Status.LOADING -> {
                    showProgress()
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    hideProgress()
                }
            }
        })
    }

    private fun removeWishListAPI(productDetail: ProductDetail, position: Int) {
        viewModel.removeFromWishAPI(
            PreferenceHandler.getToken(context).toString(),
            productDetail.id
        )
        viewModel.getRemoveFromWishAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "removeWishListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            listItem.get(position)!!.is_in_wishlist = false;
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
        viewModel.addToWishAPI(PreferenceHandler.getToken(context).toString(), productDetail.id)
        viewModel.getAddToWishAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "addToWishListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            listItem.get(position)!!.is_in_wishlist = true;
                            adapterRecycler.notifyItemChanged(position)
                            showSnackBar("Product added to wishlist")

                        } catch (e: Exception) {
                            Log.d(TAG, "addToWishListAPI:Error ${e.message}")
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


}