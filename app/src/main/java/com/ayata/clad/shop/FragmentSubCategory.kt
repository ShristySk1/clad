package com.ayata.clad.shop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentSubCategoryBinding
import com.ayata.clad.productlist.FragmentProductList
import com.ayata.clad.shop.adapter.AdapterSubCategoryFilterable
import com.ayata.clad.shop.response.ChildCategory
import com.ayata.clad.shop.response.SubCategory

class FragmentSubCategory : Fragment(), AdapterSubCategoryFilterable.OnSearchClickListener {
val TAG:String="FragmentSubCategory"
    private lateinit var binding: FragmentSubCategoryBinding
    private lateinit var adapterSubCategory: AdapterSubCategoryFilterable
    private var listChildCategory = ArrayList<ChildCategory>()
    private lateinit var subCategory: SubCategory
    private var title=""
companion object{
    val CHILD_CATEGORY="CHILD_CATEGORY"
    val CATEGORY_TITLE="CATEGORY_TITLE"

}
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSubCategoryBinding.inflate(inflater, container, false)
        listChildCategory.clear()
        if (arguments != null) {
            Log.d(TAG, "onCreateView: not null");
            val bundle = arguments
            subCategory = bundle!!.getSerializable(FragmentShop.SUB_CATEGORY) as SubCategory
            title=bundle!!.getString(FragmentShop.CATEGORY_TITLE,"")
            listChildCategory.addAll(subCategory.childCategories as ArrayList<ChildCategory>)
        }else{
            Log.d(TAG, "onCreateView: null");
        }
        initAppbar()
        initRecycler()
        prepareList()
        return binding.root
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = subCategory.title,
            textDescription = ""
        )
    }

    private fun initRecycler() {
        adapterSubCategory = AdapterSubCategoryFilterable(context, listChildCategory, this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = adapterSubCategory
        }
        Log.d(TAG, "initRecycler: ");
        adapterSubCategory.notifyDataSetChanged()
    }

    private fun prepareList() {
//        listChildCategory.clear()
//        val image="https://media.istockphoto.com/photos/female-fashion-clothes-flat-lay-square-picture-id1160533209?k=20&m=1160533209&s=612x612&w=0&h=pA12--4dUFXbYtOIOTCiMXvM77kGlUTwQWkAO3D0_Qk="
//        listChildCategory.add(ModelShop("Clothing","",image))
//        listChildCategory.add(ModelShop("Accessories","",image))
//        listChildCategory.add(ModelShop("Shoes","",image))
//        listChildCategory.addAll()
//
//        adapterSubCategory.setData(listChildCategory)

    }

    override fun onSearchClick(data: ChildCategory) {
        val bundle=Bundle()
        bundle.putSerializable(CHILD_CATEGORY,data)
        bundle.putString(CATEGORY_TITLE,title)
        val fragment=FragmentProductList()
        fragment.arguments=bundle
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
            .addToBackStack(null).commit()
    }


}