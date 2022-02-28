package com.ayata.clad.search

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentSearchBinding
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.search.adapter.AdapterRecentSearch
import com.ayata.clad.utils.Constants
import com.ayata.clad.view_all.adapter.AdapterViewAllProduct
import com.ayata.clad.view_all.model.ModelViewAllProduct
import com.google.android.material.snackbar.Snackbar


class FragmentSearch : Fragment(),AdapterViewAllProduct.OnItemClickListener,AdapterRecentSearch.OnItemClickListener {

    private lateinit var binding:FragmentSearchBinding
    private var listItem=ArrayList<ModelViewAllProduct?>()
    private lateinit var adapterRecycler:AdapterViewAllProduct

    private lateinit var adapterRecentSearch: AdapterRecentSearch
    private var listRecent=ArrayList<String>()
    var isLoading = false

    private var listImage= arrayListOf<String>("https://freepngimg.com/thumb/categories/627.png",
    "https://images.squarespace-cdn.com/content/v1/566e100d0e4c116bdc11b2c2/1473302788755-FL48S6YFWHYC9KU18K52/245282-ceb4145ac7b646889a16b6f5dbd2f455.png?format=750w",
    "https://www.pngkit.com/png/full/70-704028_running-shoes-png-image-running-shoes-clipart-transparent.png")

    var title:String=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSearchBinding.inflate(inflater, container, false)

        val bundle=arguments
        if(bundle!=null){
            title=bundle.getString(Constants.FILTER_HOME,"")
        }
        initAppbar()
        initRecycler()
        populateData()
        initScrollListener()

        initView()
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(false)
    }

    private fun initView(){
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
        binding.clearAllBtn.setOnClickListener {
            listRecent.clear()
            adapterRecentSearch.notifyDataSetChanged()
        }

        binding.btnClose.visibility=View.GONE
        binding.btnClose.setOnClickListener {
            binding.textInputSearch.editText!!.text.clear()
            binding.btnClose.visibility=View.GONE
        }

        binding.textInputSearch.editText!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                binding.btnClose.visibility=View.VISIBLE
                if(count==0){
                    binding.btnClose.visibility=View.GONE
                }
            }
        })
    }

    private fun initRecycler(){
        adapterRecycler= AdapterViewAllProduct(context,listItem,this)
        binding.recyclerView.apply {
            layoutManager=GridLayoutManager(context,2)
            adapter=adapterRecycler
        }

        adapterRecentSearch= AdapterRecentSearch(requireContext(),listRecent,this)
        binding.recyclerViewRecent.apply {
            layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            adapter=adapterRecentSearch
        }
        populateRecentSearch()
    }

    private fun populateRecentSearch(){
        listRecent.clear()

        listRecent.add("Shoes for men")
        listRecent.add("Running")
        adapterRecentSearch.notifyDataSetChanged()
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
        val snackBar = if(isOnWishList) {
            Snackbar
                .make(binding.root, "Product removed from wishlist", Snackbar.LENGTH_SHORT)
        }else{
            Snackbar
                .make(binding.root, "Product added to wishlist", Snackbar.LENGTH_SHORT)
        }
//                .setAction("RETRY") { }
        snackBar.setActionTextColor(Color.WHITE)
        snackBar.show()
        listItem[position]!!.isOnWishList=(!isOnWishList)
        adapterRecycler.notifyItemChanged(position)
    }

    override fun onCloseClicked(data: String, position: Int) {
        listRecent.removeAt(position)
        adapterRecentSearch.notifyDataSetChanged()
    }

    override fun onRecentSearchClicked(data: String, position: Int) {
    }

}