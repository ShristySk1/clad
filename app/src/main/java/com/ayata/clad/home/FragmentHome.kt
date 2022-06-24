package com.ayata.clad.home

import android.content.Intent
import android.os.Bundle
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.brand.BrandDetailActivity
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentHomeBinding
import com.ayata.clad.home.adapter.*
import com.ayata.clad.home.model.ModelNewSubscription
import com.ayata.clad.home.response.*
import com.ayata.clad.home.viewmodel.HomeViewModel
import com.ayata.clad.home.viewmodel.HomeViewModelFactory
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.productlist.FragmentProductList
import com.ayata.clad.shop.FragmentSubCategory
import com.ayata.clad.shop.response.ChildCategory
import com.ayata.clad.story.StoryActivity
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.view_all.FragmentViewAllBrand
import com.ayata.clad.view_all.FragmentViewAllProduct
import com.google.gson.Gson
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations


class FragmentHome : Fragment(), AdapterPopularMonth.OnItemClickListener,
    AdapterRecommended.OnItemClickListener, AdapterPopularBrands.OnItemClickListener,
    AdapterJustDropped.OnItemClickListener, AdapterMostPopular.OnItemClickListener,
    AdapterNewSubscription.OnItemClickListener, AdapterStories.OnItemClickListener,
    AdapterBanner.OnItemClickListener {

    companion object {
        val PRODUCT_DETAIL_ID: String = "product id"
        private const val TAG = "FragmentHome"
        const val PRODUCT_DETAIL = "product detail"
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    private var listStory = ArrayList<Story>()
    private lateinit var adapterStories: AdapterStories

    private lateinit var adapterPopularMonth: AdapterPopularMonth
    private var listPopularMonth = ArrayList<ProductDetail>()

    private lateinit var adapterRecommended: AdapterRecommended
    private var listRecommended = ArrayList<ProductDetail>()

    private lateinit var adapterPopularBrands: AdapterPopularBrands
    private var listPopularBrands = ArrayList<Brand>()

    private lateinit var adapterJustDropped: AdapterJustDropped
    private var listJustDropped = ArrayList<ProductDetail>()

    private lateinit var adapterMostPopular: AdapterMostPopular
    private var listMostPopular = ArrayList<ProductDetail>()

    private lateinit var adapterNewSubscription: AdapterNewSubscription
    private var listNewSubscription = ArrayList<ModelNewSubscription>()

    private lateinit var adapterBanner: AdapterBanner
    private var listBanner = ArrayList<Slider>()
    private var advertiseListBelowSlider = ArrayList<Advertisement>()
    private var advertiseListAboveJustDropped = ArrayList<Advertisement>()
    private lateinit var adapterAdvertiseBelowSlider: AdapterAdvertisement
    private lateinit var adapterAdvertiseAboveJustDropped: AdapterAdvertisement


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initRefreshLayout()
        prepareAPI()
        initButtonClick()
        initAppbar()
        initRecyclerView()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        Log.d("wishlilshcaleed", "onCreate home: ");
        viewModel.dashboardAPI(
            PreferenceHandler.getToken(requireContext()) ?: ""
        )
    }


    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(
            "",
            isSearch = true,
            isProfile = true,
            isClose = false,
            isLogo = true
        )
    }

    private fun initRefreshLayout() {
        //refresh layout on swipe
        binding.swipeRefreshLayout.setOnRefreshListener(OnRefreshListener {
//            prepareAPI()
            viewModel.dashboardAPI(
                PreferenceHandler.getToken(requireContext()) ?: ""
            )
            binding.swipeRefreshLayout.isRefreshing = false
            setShimmerLayout(true)

        })
        //Adding ScrollListener to activate swipe refresh layout
        binding.mainLayout.setOnScrollChangeListener(View.OnScrollChangeListener { view, i, i1, i2, i3 ->
            binding.swipeRefreshLayout.isEnabled = i1 == 0
        })
        binding.shimmerView.root.setOnScrollChangeListener(View.OnScrollChangeListener { view, i, i1, i2, i3 ->
            binding.swipeRefreshLayout.isEnabled = i1 == 0
        })

        // Adding ScrollListener to getting whether we're on First Item position or not
//        recyclerView_available.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                binding.swipeRefreshLayout.isEnabled = manager1.findFirstCompletelyVisibleItemPosition() == 0 // 0 is for first item position
//            }
//        })

    }

    private fun setShimmerLayout(isVisible: Boolean) {
        if (isVisible) {
            binding.mainLayout.visibility = View.GONE
            binding.shimmerFrameLayout.visibility = View.VISIBLE
            binding.shimmerFrameLayout.startShimmer()
        } else {
            binding.mainLayout.visibility = View.VISIBLE
            binding.shimmerFrameLayout.visibility = View.GONE
            binding.shimmerFrameLayout.stopShimmer()
        }
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            requireActivity(),
            HomeViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )
            .get(HomeViewModel::class.java)
    }

    private fun initRecyclerView() {
        //stories view
        adapterStories = AdapterStories(context, listStory, this)
        binding.recyclerStory.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterStories
        }
//        prepareDataForStory()

        //popular this month
        adapterPopularMonth = AdapterPopularMonth(context, listPopularMonth, this)
        binding.recyclerPopularMonth.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = adapterPopularMonth
        }
//        prepareDataForPopularMonth(null)

        //recommended
        adapterRecommended = AdapterRecommended(context, listRecommended, this)
        binding.recyclerRecommended.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = adapterRecommended
        }
//        prepareDataForRecommended(null)

        //popular brand
        adapterPopularBrands = AdapterPopularBrands(context, listPopularBrands, this)
        binding.recyclerPopularBrands.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterPopularBrands
        }
//        prepareDataForPopularBrands(null)
        //advertise above just dropped
        adapterAdvertiseAboveJustDropped = AdapterAdvertisement(
            requireContext(),
            advertiseListAboveJustDropped,
            object : AdapterAdvertisement.OnItemClickListener {
                override fun onProductClicked(data: Advertisement) {
                    data.adType?.let {
                        goToNextFromBanner(
                            data.adType,
                            brandSlug = data.brand?.slug,
                            childCategory = data.category,
                            mainTitle = data.title,
                            productId = data.product?.productId
                        )
                    } ?: run {
                        Toast.makeText(requireContext(), "null data", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        binding.imageAdvertiseAboveJustdrop.setSliderAdapter(adapterAdvertiseAboveJustDropped)
        binding.imageAdvertiseAboveJustdrop.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.imageAdvertiseAboveJustdrop.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.imageAdvertiseAboveJustdrop.startAutoCycle()
        //just dropped
        adapterJustDropped = AdapterJustDropped(context, listJustDropped, this)
        binding.recyclerJustDropped.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterJustDropped
        }
//        prepareDataForJustDropped(null)

        //most popular
        adapterMostPopular = AdapterMostPopular(context, listMostPopular, this)
        binding.recyclerMostPopular.apply {
            layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
            adapter = adapterMostPopular
        }
//        prepareDataForMostPopular(null)

        //new subscription
//        adapterNewSubscription = AdapterNewSubscription(context, listNewSubscription, this)
//        binding.recyclerNewSubscription.apply {
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            adapter = adapterNewSubscription
//        }
//        prepareDataForNewSubscription()
        //banner
        adapterBanner = AdapterBanner(requireContext(), listBanner, this)
        binding.imageSlider.setSliderAdapter(adapterBanner)
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.imageSlider.startAutoCycle()
        //advertise below slider
        adapterAdvertiseBelowSlider = AdapterAdvertisement(
            requireContext(),
            advertiseListBelowSlider,
            object : AdapterAdvertisement.OnItemClickListener {
                override fun onProductClicked(data: Advertisement) {
                    data.adType?.let {
                        goToNextFromBanner(
                            data.adType,
                            brandSlug = data.brand?.slug,
                            childCategory = data.category,
                            mainTitle = data.title,
                            productId = data.product?.productId
                        )
                    } ?: run {
                        Toast.makeText(requireContext(), "null data", Toast.LENGTH_SHORT).show()
                    }
                }

            })
        binding.imageAdvertiseBelowSlider.setSliderAdapter(adapterAdvertiseBelowSlider)
        binding.imageAdvertiseBelowSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.imageAdvertiseBelowSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.imageAdvertiseBelowSlider.startAutoCycle()



    }

    private fun prepareBanner(listGiven: List<Slider>?) {
        listBanner.clear()
//        listBanner.add("https://www.thoughtco.com/thmb/C7RiS4QG5TXcBG2d_Sh9i4hFpg0=/3620x2036/smart/filters:no_upscale()/close-up-of-clothes-hanging-in-row-739240657-5a78b11f8e1b6e003715c0ec.jpg")
//        listBanner.add("https://st.depositphotos.com/1003633/2284/i/600/depositphotos_22848360-stock-photo-fashion-clothes-hang-on-a.jpg")
//        listBanner.add("https://cdn.stocksnap.io/img-thumbs/280h/white-sneakers_EA7TDORJBT.jpg")

        if (!listGiven.isNullOrEmpty()) {
            listBanner.addAll(listGiven)
        }
        if (listBanner.isNullOrEmpty()) {
            binding.imageSlider.visibility = View.GONE
        } else {
            binding.imageSlider.visibility = View.VISIBLE
        }
        adapterBanner.notifyDataSetChanged()
    }

    private fun prepareDataForNewSubscription() {
//        listNewSubscription.clear()
//        listNewSubscription.add(
//            ModelNewSubscription(
//                "https://image.made-in-china.com/202f0j00gqjRIDFdribc/Autumn-and-Winter-Hand-Made-Double-Sided-Woolen-Cashmere-Ladies-Wool-Coat.jpg",
//                "Beige bliss"
//            )
//        )
//        listNewSubscription.add(
//            ModelNewSubscription(
//                "https://www.hergazette.com/wp-content/uploads/2020/01/Stylish-Photography-Poses-For-Girls-11.jpg",
//                "Silk lure"
//            )
//        )
//
//        adapterNewSubscription.notifyDataSetChanged()

    }

    private fun prepareDataForStory(stories: List<Story>) {
        listStory.clear()
        listStory.addAll(stories)
        adapterStories.notifyDataSetChanged()

    }

    private fun prepareDataForPopularMonth(listGiven: List<ProductDetail>?) {
        listPopularMonth.clear()

        if (listGiven != null) {
            listPopularMonth.addAll(listGiven)
        }
        if (listPopularMonth.size > 0) {

            adapterPopularMonth.notifyDataSetChanged()
        } else {
            binding.text1.visibility = View.GONE
            binding.popularViewBtn.visibility = View.GONE
        }
    }

    private fun prepareDataForRecommended(listGiven: List<ProductDetail>?) {
        listRecommended.clear()
        if (listGiven != null) {
            listRecommended.addAll(listGiven)
        }
        if (listRecommended.size > 0) {

            adapterRecommended.notifyDataSetChanged()
        } else {
            binding.text2.visibility = View.GONE
            binding.recommendedViewBtn.visibility = View.GONE
        }

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

    private fun prepareDataForPopularBrands(listGiven: List<Brand>?) {

        listPopularBrands.clear()
        if (listGiven != null) {
            listPopularBrands.addAll(listGiven)
        }
        if (listPopularBrands.size > 0) {
            adapterPopularBrands.notifyDataSetChanged()
        } else {
            //set visibility gone for title and view all
            binding.text4.visibility = View.GONE
            binding.popularBrandViewBtn.visibility = View.GONE
        }

    }

    private fun prepareDataForJustDropped(listGiven: List<ProductDetail>?) {
        listJustDropped.clear()

        if (listGiven != null) {
            listJustDropped.addAll(listGiven)
        }
        if (listJustDropped.size > 0) {


            adapterJustDropped.notifyDataSetChanged()
        } else {
            binding.text5.visibility = View.GONE
            binding.justDroppedViewBtn.visibility = View.GONE
        }

    }

    private fun prepareDataForAdvertisement(advertisements: List<Advertisement>) {
        advertiseListBelowSlider.clear()
        advertiseListAboveJustDropped.clear()

        for (ad in advertisements) {
            Log.d("testmydata", "prepareDataForAdvertisement: " + ad.adPosition);
            when (ad.adPosition) {
                "BELOW_SLIDER_LEFT" -> {
                    advertiseListBelowSlider.add(0, ad)
                }
                "BELOW_SLIDER_RIGHT" -> {
                    advertiseListBelowSlider.add(1, ad)
                }
                "ABOVE_NEW_ARRIVALS_LEFT" -> {
                    advertiseListAboveJustDropped.add(0, ad)
                }
                "ABOVE_NEW_ARRIVALS_RIGHT" -> {
                    advertiseListAboveJustDropped.add(1, ad)
                }
                else -> {
                    Log.d("exceptionalpositioon", "prepareDataForAdvertisement: " + ad.adPosition);
                }
            }
        }
        if (advertiseListAboveJustDropped.size > 0) {
            binding.adtype1.visibility = View.VISIBLE
        } else {
            binding.adtype1.visibility = View.GONE
        }
        adapterAdvertiseAboveJustDropped.notifyDataSetChanged()
        adapterAdvertiseBelowSlider.notifyDataSetChanged()
    }

    private fun prepareDataForMostPopular(listGiven: List<ProductDetail>?) {
        listMostPopular.clear()
        if (listGiven != null) {
            listMostPopular.addAll(listGiven)
        }
        if (listMostPopular.size > 0) {

            adapterMostPopular.notifyDataSetChanged()
        } else {
            binding.text6.visibility = View.GONE
            binding.mostPopularViewBtn.visibility = View.GONE
        }

    }

    override fun onPopularMonthClicked(data: ProductDetail, position: Int) {
        val bundle = Bundle()
        bundle.putSerializable(PRODUCT_DETAIL, data)
        val fragmentProductDetail = FragmentProductDetail()
        fragmentProductDetail.arguments = bundle
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentProductDetail)
            .addToBackStack(null).commit()
    }

    override fun onRecommendedClicked(data: ProductDetail, position: Int) {
        val bundle = Bundle()
        bundle.putSerializable(PRODUCT_DETAIL, data)
        val fragmentProductDetail = FragmentProductDetail()
        fragmentProductDetail.arguments = bundle
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentProductDetail)
            .addToBackStack(null).commit()
    }

    override fun onPopularBrandsClicked(data: Brand, position: Int) {
        (activity as MainActivity).isFromSameActivity = false
        val intent = Intent(activity, BrandDetailActivity::class.java)
        intent.putExtra("slug", data.slug)
        startActivity(intent)
    }

    override fun onJustDroppedClicked(data: ProductDetail, position: Int) {
        val bundle = Bundle()
        bundle.putSerializable(PRODUCT_DETAIL, data)
        val fragmentProductDetail = FragmentProductDetail()
        fragmentProductDetail.arguments = bundle
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentProductDetail)
            .addToBackStack(null).commit()
    }

    override fun onMostPopularClicked(data: ProductDetail, position: Int) {
        val bundle = Bundle()
        bundle.putSerializable(PRODUCT_DETAIL, data)
        val fragmentProductDetail = FragmentProductDetail()
        fragmentProductDetail.arguments = bundle
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentProductDetail)
            .addToBackStack(null).commit()
    }

    override fun onNewSubscriptionClicked(data: ModelNewSubscription, position: Int) {
//        Toast.makeText(context,"New Subscription: ${data.title}",Toast.LENGTH_SHORT).show()
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, FragmentProductDetail())
            .addToBackStack(null).commit()
    }

    override fun onStoryClick(data: Story, position: Int) {
        //story
        (activity as MainActivity).isFromSameActivity = false
        StoryActivity.storyIndex = position
        StoryActivity.listStory.clear()
        StoryActivity.listStory.addAll(listStory)
        val i = Intent(requireContext(), StoryActivity::class.java)
        startActivity(i)
    }

    private fun initButtonClick() {
        binding.justDroppedViewBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FILTER_HOME, "just_dropped")
            val fragmentViewAllProduct = FragmentViewAllProduct()
            fragmentViewAllProduct.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, fragmentViewAllProduct)
                .addToBackStack(null).commit()
        }

        binding.mostPopularViewBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FILTER_HOME, "most_popular")
            val fragmentViewAllProduct = FragmentViewAllProduct()
            fragmentViewAllProduct.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, fragmentViewAllProduct)
                .addToBackStack(null).commit()
        }
        binding.popularViewBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FILTER_HOME, "popular_this_month")
            val fragmentViewAllProduct = FragmentViewAllProduct()
            fragmentViewAllProduct.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, fragmentViewAllProduct)
                .addToBackStack(null).commit()
        }
        binding.recommendedViewBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FILTER_HOME, "recommended")
            val fragmentViewAllProduct = FragmentViewAllProduct()
            fragmentViewAllProduct.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, fragmentViewAllProduct)
                .addToBackStack(null).commit()
        }
        binding.popularBrandViewBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, FragmentViewAllBrand())
                .addToBackStack(null).commit()
        }
    }

    private fun prepareAPI() {


//        GlobalScope.launch(Dispatchers.IO) {
//            DataStoreManager(requireContext()).getToken().catch { e ->
//                e.printStackTrace()
//            }.collect {
//                withContext(Dispatchers.Main) {
//                    val token=it
//
//                }
//            }
//        }
//
        setShimmerLayout(true)
        viewModel.getDashboardAPI().observe(viewLifecycleOwner, {
            Log.d("tesshimmer", "home: " + it.status);
            when (it.status) {
                Status.SUCCESS -> {
                    setShimmerLayout(false)

                    hideError()

                    Log.d(TAG, "home: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val homeResponse =
                                Gson().fromJson<HomeResponse>(jsonObject, HomeResponse::class.java)
                            if (homeResponse.details != null) {
                                val detail = homeResponse.details
                                prepareDataForPopularBrands(detail.brands)
                                prepareDataForRecommended(detail.recommended)
                                prepareDataForJustDropped(detail.justDropped)
                                prepareDataForMostPopular(detail.mostPopular)
                                prepareDataForPopularMonth(detail.popularThisMonth)
                                prepareBanner(detail.sliders)
                                prepareDataForStory(detail.stories)
                                prepareDataForAdvertisement(detail.advertisements)
                            }
                        } catch (e: Exception) {
                            showError(e.message.toString())
                        }
                    }

                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    //Handle Error
                    setShimmerLayout(false)
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "home: ${it.message}")
                    showError(it.message.toString())
                }
            }
        })
    }


    private fun hideError() {
        binding.relativeLayout.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.relativeLayout.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.error.errorImage.setImageResource(Constants.ERROR_SERVER)
        binding.error.errorDescription.text = message
    }

    override fun onBannerClicked(data: Slider) {
//        val bundle=Bundle()
//        bundle.putString(Constants.FILTER_HOME,data.title)
//        val fragmentViewAllProduct=FragmentViewAllProduct()
//        fragmentViewAllProduct.arguments=bundle
//        parentFragmentManager.beginTransaction().replace(R.id.main_fragment,fragmentViewAllProduct)
//            .addToBackStack(null).commit()

        data.sliderType?.let {
            goToNextFromBanner(
                data.sliderType,
                brandSlug = data.brand.slug,
                childCategory = data.category,
                mainTitle = data.title,
                productId = data.product?.productId
            )
        } ?: run { Toast.makeText(requireContext(), "null data", Toast.LENGTH_SHORT).show() }

    }

    fun goToNextFromBanner(
        sliderType: String,
        childCategory: ChildCategory?,
        mainTitle: String,
        productId: Int?,
        brandSlug: String?
    ) {
        val bundle = Bundle()
        when (sliderType) {
            "Category" -> {
                bundle.putSerializable(FragmentSubCategory.CHILD_CATEGORY, childCategory ?: return)
                bundle.putString(FragmentSubCategory.CATEGORY_TITLE, mainTitle)
                val fragment = FragmentProductList()
                fragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .addToBackStack(null).commit()
            }
            "Product" -> {
                bundle.putInt(PRODUCT_DETAIL_ID, productId ?: return)
                val fragmentProductDetail = FragmentProductDetail()
                fragmentProductDetail.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragmentProductDetail)
                    .addToBackStack(null).commit()
            }
            "Brand" -> {
                (activity as MainActivity).isFromSameActivity = false
                val intent = Intent(activity, BrandDetailActivity::class.java)
                intent.putExtra("slug", brandSlug ?: return)
                startActivity(intent)
            }
            else -> {
                Toast.makeText(requireContext(), sliderType, Toast.LENGTH_SHORT).show()
            }
        }
    }
}