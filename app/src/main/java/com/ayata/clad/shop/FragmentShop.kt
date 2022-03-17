package com.ayata.clad.shop

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentShopBinding
import com.ayata.clad.shop.adapter.AdapterShopFilterable
import com.ayata.clad.shop.response.Category
import com.ayata.clad.shop.response.CategoryResponse
import com.ayata.clad.shop.response.SubCategory
import com.ayata.clad.shopping_bag.viewmodel.CategoryViewModel
import com.ayata.clad.shopping_bag.viewmodel.CategoryViewModelFactory
import com.ayata.clad.utils.MyLayoutInflater
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson


class FragmentShop : Fragment(), AdapterShopFilterable.OnSearchClickListener {

    companion object {
        const val SUB_CATEGORY = "child name"
        const val CATEGORY_TITLE = "CATEGORY_TITLE"
    }

    private val TAG: String = "FragmentShop"
    private lateinit var binding: FragmentShopBinding
    private lateinit var viewModel: CategoryViewModel

    private lateinit var adapterShopFilterable: AdapterShopFilterable
    private var shopRecyclerList = ArrayList<SubCategory>()

    private var listCategory = ArrayList<String>()
    private var apiCategoryList = ArrayList<Category>()
    private var title = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentShopBinding.inflate(inflater, container, false)
        initAppbar()
        setUpViewModel()
        initSearchView()
        initRecycler()
//        initTabLayout()
        getCategoryAPI()
        return binding.root
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            CategoryViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[CategoryViewModel::class.java]
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(
            getString(R.string.shop),
            isSearch = false,
            isProfile = true,
            isClose = false
        )
    }

    private fun initSearchView() {

        binding.searchText.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapterShopFilterable.filter.filter(p0)
            }

            override fun afterTextChanged(p0: Editable?) {
                adapterShopFilterable.filter.filter(p0)
            }
        })

//        binding.layoutSearch.setOnClickListener {
//            parentFragmentManager.beginTransaction().replace(R.id.main_fragment,FragmentProductList())
//                .addToBackStack(null).commit()
//        }

    }

    private fun initTabLayout(categoryist: ArrayList<Category>) {
        apiCategoryList = categoryist;
        listCategory.clear()
        for (c in categoryist) {
            listCategory.add(c.category)
        }
        val tabLayout = binding.tabLayout
        tabLayout.removeAllTabs()
        for (tabString in listCategory) {
            tabLayout.addTab(tabLayout.newTab().setText(tabString.toUpperCase()))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    setStyleForTab(it, Typeface.BOLD)
                }
                val tabString = tab!!.text
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
                val tabString = tab!!.text
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

    private fun tabCustomise() {
        //margin between two tabs with background
        val tabs = binding.tabLayout.getChildAt(0) as ViewGroup
        for (i in 0 until tabs.childCount) {
            val tab = tabs.getChildAt(i)
            val layoutParams = tab.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 1f
            layoutParams.marginEnd = 15
            layoutParams.marginStart = 15
            layoutParams.topMargin = 32
            layoutParams.bottomMargin = 32
            tab.layoutParams = layoutParams
            binding.tabLayout.requestLayout()
        }

    }

    private fun initRecycler() {
        adapterShopFilterable = AdapterShopFilterable(context, shopRecyclerList, this)
        binding.recyclerView.apply {
            adapter = adapterShopFilterable
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun prepareCategoryTabList(category: String) {
//        val listWomen = ArrayList<ModelShop>()
//        val listMen = ArrayList<ModelShop>()
//        val listKids = ArrayList<ModelShop>()
//
//        listWomen.add(
//            ModelShop(
//                "New Arrivals",
//                "Upcoming: June 2021",
//                "https://www.coverstory.co.in/media/cms/home/category/work.jpg"
//            )
//        )
//        listWomen.add(
//            ModelShop(
//                "On Sale",
//                "Up to 40% off",
//                "https://www.coverstory.co.in/media/cms/home/category/work.jpg"
//            )
//        )
//        listWomen.add(
//            ModelShop(
//                "Clothing",
//                "",
//                "https://www.coverstory.co.in/media/cms/home/category/work.jpg"
//            )
//        )
//        listWomen.add(
//            ModelShop(
//                "Accessories",
//                "",
//                "https://www.coverstory.co.in/media/cms/home/category/work.jpg"
//            )
//        )
//        listWomen.add(
//            ModelShop(
//                "Kurthas",
//                "",
//                "https://www.coverstory.co.in/media/cms/home/category/work.jpg"
//            )
//        )
//
//        listMen.add(
//            ModelShop(
//                "New Arrivals",
//                "Upcoming: June 2021",
//                "https://images.pexels.com/photos/842811/pexels-photo-842811.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
//            )
//        )
//        listMen.add(
//            ModelShop(
//                "On Sale",
//                "Up to 30% off",
//                "https://images.pexels.com/photos/842811/pexels-photo-842811.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
//            )
//        )
//        listMen.add(
//            ModelShop(
//                "Shoes",
//                "",
//                "https://images.pexels.com/photos/842811/pexels-photo-842811.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
//            )
//        )
//
//        listKids.add(
//            ModelShop(
//                "New Arrivals",
//                "Upcoming: June 2021",
//                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSklPj6OR9Qecp-ilpT0UH3yzfx-ngRbADw3g&usqp=CAU"
//            )
//        )
//        listKids.add(
//            ModelShop(
//                "On Sale",
//                "Up to 80% off",
//                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSklPj6OR9Qecp-ilpT0UH3yzfx-ngRbADw3g&usqp=CAU"
//            )
//        )
//        listKids.add(
//            ModelShop(
//                "Toys",
//                "",
//                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSklPj6OR9Qecp-ilpT0UH3yzfx-ngRbADw3g&usqp=CAU"
//            )
//        )
        title = category
        shopRecyclerList.clear()

        val getSpecificCat = apiCategoryList.filter {
            it.category.toLowerCase() == category.toLowerCase()
        }.single()
        val listSpecificSubCat = getSpecificCat.subCategory
        if (listSpecificSubCat.size == 0) {
            //empty list
            MyLayoutInflater().onAddField(requireContext(), binding.root, R.layout.layout_error,R.drawable.ic_cart,"Empty!","No products available")
        } else {
            if (binding.root.findViewById<LinearLayout>(R.id.layout_root) != null) {
                MyLayoutInflater().onDelete(
                    binding.root,
                    binding.root.findViewById(R.id.layout_root)
                )
            }
                shopRecyclerList.addAll(listSpecificSubCat)
        }
//        adapterShopFilterable.setData(shopRecyclerList)
        adapterShopFilterable.notifyDataSetChanged()

    }

    override fun onSearchClick(data: SubCategory) {
//        Toast.makeText(context,data.name,Toast.LENGTH_SHORT).show()
        val fragmentSubCategory = FragmentSubCategory()
        val bundle = Bundle()
        bundle.putSerializable(SUB_CATEGORY, data)
        bundle.putString(CATEGORY_TITLE, title)
        fragmentSubCategory.arguments = bundle
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentSubCategory)
            .addToBackStack(null).commit()
    }

    private fun getCategoryAPI() {
//        setShimmerLayout(true)

        viewModel.getCategoryListAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    hideError()
                    setShimmerLayout(false)
                    Log.d(TAG, "getCartAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val message = jsonObject.get("message").asString
                            if (message.contains("empty.", true)) {
//                                setUpView()
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "getCategoryAPI:Error ${e.message}")
                            try {
                                val categoryResponse =
                                    Gson().fromJson<CategoryResponse>(
                                        jsonObject,
                                        CategoryResponse::class.java
                                    )
                                if (categoryResponse.categories != null) {
                                    if (categoryResponse.categories.size > 0) {
                                        val categoryist = categoryResponse.categories
                                        initTabLayout(categoryist as ArrayList<Category>)
                                    } else {
//                                        setUpView()
                                    }

                                }
                            } catch (e: Exception) {
                                Log.d(TAG, "getWishListAPI:Error ${e.message}")
                            }
                        }
                    }
                    adapterShopFilterable.notifyDataSetChanged()
                }
                Status.LOADING -> {
                    setShimmerLayout(true)
                }
                Status.ERROR -> {
                    //Handle Error
                    setShimmerLayout(false)
//                    listCheckout.clear()
                    showError(it.message.toString())

                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "getCartAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun showError(it:String) {
        binding.rootContainer.visibility=View.GONE
        MyLayoutInflater().onAddField(requireContext(), binding.root, R.layout.layout_error,R.drawable.ic_cart,"Error!",it)

    }
    private fun hideError() {
        binding.rootContainer.visibility=View.VISIBLE
        if (binding.root.findViewById<LinearLayout>(R.id.layout_root) != null) {
            MyLayoutInflater().onDelete(
                binding.root,
                binding.root.findViewById(R.id.layout_root)
            )
        }
    }

    private fun setShimmerLayout(isVisible: Boolean) {
        if (isVisible) {
            binding.shimmerFrameLayout.visibility = View.VISIBLE
            binding.shimmerFrameLayout.startShimmer()
        } else {
            binding.shimmerFrameLayout.visibility = View.GONE
            binding.shimmerFrameLayout.stopShimmer()
        }
    }
}