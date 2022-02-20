package com.ayata.clad.view_all

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.ayata.clad.view_all.adapter.AdapterViewAllProduct
import com.ayata.clad.view_all.model.ModelViewAllProduct
import com.ayata.clad.view_all.viewmodel.BrandAllViewModel
import com.ayata.clad.view_all.viewmodel.BrandAllViewModelFactory
import com.ayata.clad.view_all.viewmodel.ProductAllViewModel
import com.ayata.clad.view_all.viewmodel.ProductAllViewModelFactory
import com.ayata.clad.wishlist.FragmentWishlist
import com.google.android.material.snackbar.Snackbar


class FragmentViewAllProduct : Fragment(),AdapterViewAllProduct.OnItemClickListener {

    companion object{
        private const val TAG="FragmentViewAllProduct"
    }
    private lateinit var binding:FragmentViewAllProductBinding
    private var listItem=ArrayList<ModelViewAllProduct?>()
    private lateinit var adapterRecycler:AdapterViewAllProduct
    var isLoading = false
    private lateinit var viewModel: ProductAllViewModel


    private var listImage= arrayListOf<String>("https://freepngimg.com/thumb/categories/627.png",
    "https://images.squarespace-cdn.com/content/v1/566e100d0e4c116bdc11b2c2/1473302788755-FL48S6YFWHYC9KU18K52/245282-ceb4145ac7b646889a16b6f5dbd2f455.png?format=750w",
    "https://www.pngkit.com/png/full/70-704028_running-shoes-png-image-running-shoes-clipart-transparent.png")

    var title:String=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentViewAllProductBinding.inflate(inflater, container, false)

        val bundle=arguments
        if(bundle!=null){
            title=bundle.getString(Constants.FILTER_HOME,"")
        }
        initViewModel()
        initAppbar()
        initRecycler()
        populateData()
        initScrollListener()
        getProductListAPI(0,title)
        return binding.root
    }

    private fun initViewModel(){
        viewModel= ViewModelProvider(this,
            ProductAllViewModelFactory(ApiRepository(ApiService.getInstance()))
        )[ProductAllViewModel::class.java]
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(title,
            isSearch = true,
            isProfile = false,
            isClose = false,
            isLogo = false
        )
    }

    private fun initRecycler(){
        adapterRecycler= AdapterViewAllProduct(context,listItem,this)
        binding.recyclerView.apply {
            layoutManager=GridLayoutManager(context,2)
            adapter=adapterRecycler
        }
    }

    private fun populateData() {
        listItem.clear()
        var i = 0
        while (i < 10) {
            listItem.add(ModelViewAllProduct("Product $i","500.0","150","Company ABC",false,listImage.random()))
            i++
        }
    }


    private fun initScrollListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                if (!isLoading) {
                    if (layoutManager != null &&
                        layoutManager.findLastCompletelyVisibleItemPosition() == listItem.size - 1) {
                        //bottom of list!
                        loadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        listItem.add(null)
        adapterRecycler.notifyItemInserted(listItem.size - 1)
        val handler = Handler()
        handler.postDelayed({
            listItem.removeAt(listItem.size - 1)
            val scrollPosition: Int = listItem.size
            adapterRecycler.notifyItemRemoved(scrollPosition)
            var currentSize = scrollPosition
            val nextLimit = currentSize + 9
            Log.d("infinitescroll", "loadMore: $scrollPosition --$nextLimit")
            while (currentSize - 1 < nextLimit) {
                listItem.add(ModelViewAllProduct("Product $currentSize","500.0","80",
                    "Company ABC",false,listImage.random()))
                currentSize++
            }
            adapterRecycler.notifyDataSetChanged()
            isLoading = false
        }, 2000)
    }

    override fun onProductClickListener(data: ModelViewAllProduct) {
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment,
            FragmentProductDetail()).addToBackStack(null).commit()
    }

    override fun onWishListClicked(data: ModelViewAllProduct, position: Int) {
        val isOnWishList=listItem[position]!!.isOnWishList
//        api call
//        if (isOnWishList) {
//            removeWishListAPI(data)
//        } else {
//            addToWishListAPI(data)
//        }
        val snackBar = if(isOnWishList) {
            Snackbar
                .make(binding.root, "Product removed from wishlist", Snackbar.LENGTH_SHORT)
        }else{
            Snackbar
                .make(binding.root, "Product added to wishlist", Snackbar.LENGTH_SHORT)
        }
        snackBar.setActionTextColor(Color.WHITE)
        snackBar.show()
        listItem[position]!!.isOnWishList=(!isOnWishList)
        adapterRecycler.notifyItemChanged(position)
    }

    private fun getProductListAPI(offset:Int,filter:String){
//        listItem.clear()
        viewModel.productListApi(PreferenceHandler.getToken(context).toString(),offset,filter)
        viewModel.getProductListAPI().observe(viewLifecycleOwner,{
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "getProductListAPI: ${it.data}")
                    val jsonObject=it.data
                    if(jsonObject!=null){
                        try{

                        }catch (e:Exception){
                            Log.d(TAG, "getProductListAPI:Error ${e.message}")
                        }
                    }
                    adapterRecycler.notifyDataSetChanged()
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "getProductListAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun removeWishListAPI(productDetail:ProductDetail){
        viewModel.removeFromWishAPI(PreferenceHandler.getToken(context).toString(),productDetail.id)
        viewModel.getRemoveFromWishAPI().observe(viewLifecycleOwner,{
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "removeWishListAPI: ${it.data}")
                    val jsonObject=it.data
                    if(jsonObject!=null){
                        showSnackBar(msg="Product removed from wishlist")
                        try{

                        }catch (e:Exception){
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

    private fun addToWishListAPI(productDetail:ProductDetail){
        viewModel.addToWishAPI(PreferenceHandler.getToken(context).toString(),productDetail.id)
        viewModel.getAddToWishAPI().observe(viewLifecycleOwner,{
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "addToWishListAPI: ${it.data}")
                    val jsonObject=it.data
                    if(jsonObject!=null){
                        showSnackBar("Product added to wishlist")
                        try{

                        }catch (e:Exception){
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

    private fun showSnackBar(msg:String){
        val snackBar = Snackbar
            .make(binding.root, msg, Snackbar.LENGTH_SHORT)
        snackBar.setActionTextColor(Color.WHITE)
        snackBar.show()
    }



}