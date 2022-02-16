package com.ayata.clad.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentSubCategoryBinding
import com.ayata.clad.productlist.FragmentProductList
import com.ayata.clad.shop.adapter.AdapterSubCategoryFilterable
import com.ayata.clad.shop.model.ModelShop

class FragmentSubCategory : Fragment(),AdapterSubCategoryFilterable.OnSearchClickListener {

    private lateinit var binding:FragmentSubCategoryBinding
    private lateinit var adapterSubCategory: AdapterSubCategoryFilterable
    private var listSubCategory=ArrayList<ModelShop>()
    private var title:String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSubCategoryBinding.inflate(inflater, container, false)
        if(arguments!=null){
            val bundle=arguments
            title=bundle!!.getString(FragmentShop.TITLE,"")
        }
        initAppbar()
        initRecycler()
        prepareList()
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = title,
            textDescription = ""
        )
    }

    private fun initRecycler(){
        adapterSubCategory= AdapterSubCategoryFilterable(context,listSubCategory,this)
        binding.recyclerView.apply {
            layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            adapter=adapterSubCategory
        }
    }

    private fun prepareList(){
        listSubCategory.clear()
        val image="https://media.istockphoto.com/photos/female-fashion-clothes-flat-lay-square-picture-id1160533209?k=20&m=1160533209&s=612x612&w=0&h=pA12--4dUFXbYtOIOTCiMXvM77kGlUTwQWkAO3D0_Qk="
        listSubCategory.add(ModelShop("Clothing","",image))
        listSubCategory.add(ModelShop("Accessories","",image))
        listSubCategory.add(ModelShop("Shoes","",image))

        adapterSubCategory.setData(listSubCategory)
    }

    override fun onSearchClick(data: ModelShop) {
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment, FragmentProductList())
            .addToBackStack(null).commit()
    }


}