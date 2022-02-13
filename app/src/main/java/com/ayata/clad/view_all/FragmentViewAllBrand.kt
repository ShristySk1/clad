package com.ayata.clad.view_all

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentViewAllBrandBinding
import com.ayata.clad.home.model.ModelPopularBrands
import com.ayata.clad.utils.Constants
import com.ayata.clad.view_all.adapter.AdapterViewAllBrand
import com.ayata.clad.view_all.adapter.AdapterViewAllProduct
import com.ayata.clad.view_all.model.ModelViewAllProduct


class FragmentViewAllBrand : Fragment(),AdapterViewAllBrand.OnItemClickListener {

    private lateinit var binding:FragmentViewAllBrandBinding
    private lateinit var adapterViewAllBrand: AdapterViewAllBrand
    private var listBrand=ArrayList<ModelPopularBrands?>()
    var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentViewAllBrandBinding.inflate(inflater, container, false)
        initAppbar()
        populateData()
        initRecycler()
        initScrollListener()
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1("Brands",
            isSearch = false,
            isProfile = false,
            isClose = false,
            isLogo = false
        )
    }

    private var listDrawable= arrayListOf<Int>(R.drawable.brand_aamayra,
        R.drawable.brand_aroan,R.drawable.brand_bishrom,
        R.drawable.brand_caliber,R.drawable.brand_creative_touch,
        R.drawable.brand_fibro,R.drawable.brand_fuloo,
        R.drawable.brand_gofi,R.drawable.brand_goldstar,
        R.drawable.brand_hillsandclouds,R.drawable.brand_jujuwears,
        R.drawable.brand_kasa,R.drawable.brand_ktm_city,R.drawable.brand_logo,
        R.drawable.brand_mode23,R.drawable.brand_newmew,
        R.drawable.brand_phalanoluga,R.drawable.brand_sabah,
        R.drawable.brand_station,R.drawable.brand_tsarmoire)

    private fun initRecycler(){
        adapterViewAllBrand=AdapterViewAllBrand(context,listBrand,this)
        binding.recyclerView.apply {
            adapter=adapterViewAllBrand
            layoutManager= GridLayoutManager(context,2)
        }
    }
    private fun populateData() {
        listBrand.clear()
        listDrawable.shuffle()
        var i = 0
        while (i < 10) {
            listBrand.add(ModelPopularBrands(listDrawable[i],"Brand $i",""))
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
                        layoutManager.findLastCompletelyVisibleItemPosition() == listBrand.size - 1) {
                        //bottom of list!
                        loadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        listBrand.add(null)
        adapterViewAllBrand.notifyItemInserted(listBrand.size - 1)
        val handler = Handler()
        handler.postDelayed({
            listBrand.removeAt(listBrand.size - 1)
            val scrollPosition: Int = listBrand.size
            adapterViewAllBrand.notifyItemRemoved(scrollPosition)
            var currentSize = scrollPosition
            val nextLimit = currentSize + 9
            Log.d("infinitescroll", "loadMore: $scrollPosition --$nextLimit")

            while (currentSize - 1 < nextLimit) {
                if(currentSize<listDrawable.size){
                    listBrand.add(ModelPopularBrands(listDrawable[currentSize],"Brand $currentSize",""))
                }else{
                    break
                }
                currentSize++
            }
            adapterViewAllBrand.notifyDataSetChanged()
            isLoading = false
        }, 2000)
    }

    override fun onBrandClickListener(data: ModelPopularBrands) {
        val bundle=Bundle()
        bundle.putString(Constants.FILTER_HOME,data.title)
        val fragmentViewAllProduct=FragmentViewAllProduct()
        fragmentViewAllProduct.arguments=bundle
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment,fragmentViewAllProduct)
            .addToBackStack(null).commit()
    }

}