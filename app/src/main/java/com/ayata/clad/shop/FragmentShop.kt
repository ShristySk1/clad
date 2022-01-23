package com.ayata.clad.shop

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentShopBinding
import com.ayata.clad.productlist.FragmentProductList
import com.ayata.clad.shop.adapter.AdapterShopFilterable
import com.ayata.clad.shop.model.ModelShop
import com.google.android.material.tabs.TabLayout


class FragmentShop : Fragment(),AdapterShopFilterable.OnSearchClickListener {

    private lateinit var binding: FragmentShopBinding

    private lateinit var adapterShopFilterable: AdapterShopFilterable
    private var shopRecyclerList=ArrayList<ModelShop>()

    private var listCategory=ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentShopBinding.inflate(inflater, container, false)

        initAppbar()
        initSearchView()
        initRecycler()
        initTabLayout()
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(getString(R.string.shop),
            isSearch = false,
            isProfile = true,
            isClose = false
        )
    }

    private fun initSearchView(){

//        binding.searchText.editText!!.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                adapterShopFilterable.filter.filter(p0)
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                adapterShopFilterable.filter.filter(p0)
//            }
//        })

        binding.layoutSearch.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment,FragmentProductList())
                .addToBackStack(null).commit()
        }

    }

    private fun initTabLayout(){
        listCategory.clear()
        listCategory.add("Men")
        listCategory.add("Women")
        listCategory.add("Kids")

        val tabLayout=binding.tabLayout
        for(tabString in listCategory) {
            tabLayout.addTab(tabLayout.newTab().setText(tabString.toUpperCase()))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    setStyleForTab(it, Typeface.BOLD)
                }
                val tabString=tab!!.text
                prepareCategoryTabList(tabString.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    setStyleForTab(it, Typeface.NORMAL)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.let {
                    setStyleForTab(it, Typeface.BOLD)
                }
                val tabString=tab!!.text
                prepareCategoryTabList(tabString.toString())
            }

            //bold or normal text
            fun setStyleForTab(tab: TabLayout.Tab, style: Int) {
                tab.view.children.find { it is TextView }?.let { tv ->
                    (tv as TextView).post {
                        tv.setTypeface(null, style)
                    }
                }
            }

        })
        tabLayout.getTabAt(0)!!.select()


    }

    private fun tabCustomise(){
        //margin between two tabs with background
        val tabs = binding.tabLayout.getChildAt(0) as ViewGroup
        for (i in 0 until tabs.childCount ) {
            val tab = tabs.getChildAt(i)
            val layoutParams = tab.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 1f
            layoutParams.marginEnd = 15
            layoutParams.marginStart = 15
            layoutParams.topMargin=32
            layoutParams.bottomMargin=32
            tab.layoutParams = layoutParams
            binding.tabLayout.requestLayout()
        }

    }

    private fun initRecycler(){
        adapterShopFilterable= AdapterShopFilterable(context,shopRecyclerList,this)
        binding.recyclerView.apply {
            adapter=adapterShopFilterable
            layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        }
    }

    private fun prepareCategoryTabList(category:String){
        val listWomen=ArrayList<ModelShop>()
        val listMen=ArrayList<ModelShop>()
        val listKids=ArrayList<ModelShop>()

        listWomen.add(ModelShop("New Arrivals","Upcoming: June 2021","https://www.coverstory.co.in/media/cms/home/category/work.jpg"))
        listWomen.add(ModelShop("On Sale","Up to 40% off","https://www.coverstory.co.in/media/cms/home/category/work.jpg"))
        listWomen.add(ModelShop("Clothing","","https://www.coverstory.co.in/media/cms/home/category/work.jpg"))
        listWomen.add(ModelShop("Accessories","","https://www.coverstory.co.in/media/cms/home/category/work.jpg"))
        listWomen.add(ModelShop("Kurthas","","https://www.coverstory.co.in/media/cms/home/category/work.jpg"))

        listMen.add(ModelShop("New Arrivals","Upcoming: June 2021","https://images.pexels.com/photos/842811/pexels-photo-842811.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"))
        listMen.add(ModelShop("On Sale","Up to 30% off","https://images.pexels.com/photos/842811/pexels-photo-842811.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"))
        listMen.add(ModelShop("Shoes","","https://images.pexels.com/photos/842811/pexels-photo-842811.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"))

        listKids.add(ModelShop("New Arrivals","Upcoming: June 2021","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSklPj6OR9Qecp-ilpT0UH3yzfx-ngRbADw3g&usqp=CAU"))
        listKids.add(ModelShop("On Sale","Up to 80% off","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSklPj6OR9Qecp-ilpT0UH3yzfx-ngRbADw3g&usqp=CAU"))
        listKids.add(ModelShop("Toys","","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSklPj6OR9Qecp-ilpT0UH3yzfx-ngRbADw3g&usqp=CAU"))

        shopRecyclerList.clear()
        when {
            category.equals("men",true) -> {
                shopRecyclerList.addAll(listMen)
            }
            category.equals("women",true) -> {
                shopRecyclerList.addAll(listWomen)
            }
            else -> {
                shopRecyclerList.addAll(listKids)
            }
        }
        adapterShopFilterable.setData(shopRecyclerList)
        adapterShopFilterable.notifyDataSetChanged()

    }

    override fun onSearchClick(data: ModelShop) {
//        Toast.makeText(context,data.name,Toast.LENGTH_SHORT).show()
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment,FragmentProductList())
            .addToBackStack(null).commit()
    }


}