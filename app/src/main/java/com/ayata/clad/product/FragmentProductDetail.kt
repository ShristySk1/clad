package com.ayata.clad.product

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.DialogShoppingSizeBinding
import com.ayata.clad.databinding.FragmentProductDetailBinding
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.home.adapter.AdapterRecommended
import com.ayata.clad.home.response.HomeResponse
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.home.response.Variant
import com.ayata.clad.home.viewmodel.HomeViewModel
import com.ayata.clad.home.viewmodel.HomeViewModelFactory
import com.ayata.clad.product.adapter.AdapterColor
import com.ayata.clad.product.reviews.FragmentReview
import com.ayata.clad.product.viewmodel.ProductViewModel
import com.ayata.clad.product.viewmodel.ProductViewModelFactory
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.model.ModelCircleText
import com.ayata.clad.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


class FragmentProductDetail : Fragment(), AdapterColor.OnItemClickListener {
    private val TAG = "FragmentProductDetail"
    private lateinit var adapterCircleText: AdapterCircleText
    private lateinit var binding: FragmentProductDetailBinding
    private var listText = ArrayList<ModelCircleText>()
    private var isProductWishList: Boolean = false
    private var isProductInCart: Boolean = false
    private lateinit var viewModel: ProductViewModel
    private lateinit var productDetail: ProductDetail
    lateinit var galleryBundle: List<String>
    private var listRecommendation = ArrayList<ProductDetail>()

    private lateinit var adapterRecommended: AdapterRecommended
    private lateinit var viewModelHome: HomeViewModel
    var dynamicVarientId = 0
    var choosenSizePosition = 0
var isStockAvailable=true
    //for color and size
    lateinit var myMaps: MutableMap<String, MutableList<Variant>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        setUpViewModel()
        initView()
        getBundle()
        setUpFullScreen()
        tapToCopyListener()
        productLikedListener()
        setUpRecyclerRecommendation()
        binding.btnShare.setOnClickListener {
//            https://clad.ayata.com.np/product/details/soft-fur-jacket/
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                val shareMessage =
                    "https://clad.ayata.com.np/product/details/${productDetail.slug}"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "Share Using"))
            } catch (e: Exception) {
                Log.d(TAG, "onCreateView: " + e.message.toString());
            }
        }
        binding.imageView3.setOnClickListener {
            goToGalleryView()
        }
        binding.cardGallary.setOnClickListener {
            goToGalleryView()
        }

        return binding.root
    }

    private fun goToGalleryView() {
        val fragment = FragmentProductDetailFull2()
        val bundle = Bundle()
        bundle.putSerializable("gallary", galleryBundle as Serializable)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
//            .setCustomAnimations(
//            R.anim.enter_from_right,
//            R.anim.exit_to_left,
//            R.anim.enter_from_left,
//            R.anim.exit_to_right
//        )
            .replace(R.id.main_fragment, fragment)
            .addToBackStack(null)
            .commit()

    }

    private fun getBundle() {
        val bundle = arguments
        if (bundle != null) {
            Log.d(TAG, "getBundle: ");
            val data = bundle.getSerializable(FragmentHome.PRODUCT_DETAIL)
            if (data != null) {
                productDetail = data as ProductDetail
                galleryBundle = data.imageUrl
                setProductData()
            }
//            galleryBundle = bundle
        } else {
            Log.d(TAG, "getBundle:null ");
        }
    }

    private fun setProductData() {
        choosenSizePosition = 0
        if (PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case), true)) {
            binding.price.text = getString(R.string.rs) + " ${productDetail.price}"
            binding.oldPrice.text = getString(R.string.rs) + " ${productDetail.oldPrice}"
            binding.detail2.price.text = getString(R.string.rs) + " ${productDetail.price}"

        } else {
            binding.price.text = getString(R.string.usd) + " ${productDetail.price}"
            binding.oldPrice.text = getString(R.string.usd) + " ${productDetail.oldPrice}"
            binding.detail2.price.text = getString(R.string.usd) + " ${productDetail.price}"
        }
        if (productDetail.isCouponAvailable) {
            binding.detail2.constraintLayout.visibility = View.VISIBLE
            binding.detail2.couponTitle.text = productDetail.coupon?.title
            binding.detail2.couponDesc.text = productDetail.coupon?.description
            binding.detail2.tvTextToCopy.text = productDetail.coupon?.code//code
        } else {
            binding.detail2.constraintLayout.visibility = View.GONE
        }
        binding.name.text = productDetail.name
        binding.storeName.text = productDetail.vendor
        binding.description.text = Html.fromHtml(productDetail.description)
        binding.detail2.name.text = productDetail.name
//        isProductWishList = productDetail.isInWishlist
//        isProductInCart = productDetail.isInCart
//        Glide.with(requireContext()).load(productDetail.image_url).into(binding.imageView3)
        //reviews
        if (productDetail.reviews != null) {
            setUpTabChoose(
                productDetail.reviews.size,
                productDetail.reviews.width,
                productDetail.reviews.quality,
                productDetail.reviews.comfort
            )
        }
        val colorsize = setHashMapColorSize()
        setUpRecyclerColor(colorsize.keys)
//        setCurrentVariant()

        binding.detail2.tvViewAllReview.setOnClickListener {
            val fragment = FragmentReview.newInstance(productDetail.reviews)
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            ProductViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[ProductViewModel::class.java]
        viewModelHome = ViewModelProvider(
            requireActivity(),
            HomeViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )
            .get(HomeViewModel::class.java)
    }

    private fun productLikedListener() {
        binding.cardWish.clickWithDebounce {
            if (this::productDetail.isInitialized) {
                if (isProductWishList) {
//                    removeWishListAPI()
                } else {
                    if (PreferenceHandler.getToken(context) != "")
                        addToWishListAPI() else (activity as MainActivity).showDialogLogin()
                }
//                setWishlist()
//                isProductWishList=(!isProductWishList)
            }

        }
        binding.cardCart.clickWithDebounce {
            if (this::productDetail.isInitialized) {
                if (isProductInCart) {
                    showSnackBar("Product already added in cart", Constants.GO_TO_CART)
                } else {
                    if (PreferenceHandler.getToken(context) != "")
                        addToCartAPI() else (activity as MainActivity).showDialogLogin()
//                    addToCartAPI()
                }
//                setCart()
//                isProductInCart=(!isProductInCart)
            }

        }

        binding.detail2.ivCart.clickWithDebounce {
            if (this::productDetail.isInitialized) {
                if (isProductInCart) {
                    showSnackBar("Product already added in cart", Constants.GO_TO_CART)
                } else {
                    if (PreferenceHandler.getToken(context) != "")
                        addToCartAPI() else (activity as MainActivity).showDialogLogin()
                }
//                setCart()
//                isProductInCart=(!isProductInCart)
            }
        }
    }

    private fun showSnackBar(msg: String, flag: String) {
        ((activity) as MainActivity).showSnakbar(msg, flag)
//        requireContext().showToast(msg,true)
    }

    private fun setWishlist(isWishList: Boolean) {
        //from api
        if (isWishList) {
            binding.ivHeart.setImageResource(R.drawable.ic_heart_filled)
        } else {
            binding.ivHeart.setImageResource(R.drawable.ic_heart_outline)
        }
    }

    private fun setCart(isCart: Boolean) {
        //from api
        if (isCart) {
            binding.imageCart.setImageResource(R.drawable.ic_bag_filled)
            binding.detail2.ivCart.setImageResource(R.drawable.ic_bag_filled)
        } else {
            binding.imageCart.setImageResource(R.drawable.ic_cart)
            binding.detail2.ivCart.setImageResource(R.drawable.ic_cart)
        }
    }

    private fun tapToCopyListener() {
        binding
            .detail2.tvTapToCopy.setOnClickListener {
                val text =
                    requireContext().copyToClipboard(binding.detail2.tvTextToCopy.text.toString())
                Log.d(TAG, "tapToCopyListener: $text")
                val toast = Toast.makeText(
                    requireContext(),
                    "Copied to Clipboard!", Toast.LENGTH_SHORT
                )
//                toast.setGravity(Gravity.BOTTOM or Gravity.RIGHT, 50, 50)
                toast.show()
            }
        binding
            .detail2.tvTextToCopy.setOnClickListener {
                val text =
                    requireContext().copyToClipboard(binding.detail2.tvTextToCopy.text.toString())
                Log.d(TAG, "tapToCopyListener: $text")
                val toast = Toast.makeText(
                    requireContext(),
                    "Copied to Clipboard!", Toast.LENGTH_SHORT
                )
//                toast.setGravity(Gravity.BOTTOM or Gravity.RIGHT, 50, 50)
                toast.show()
            }
    }

    private fun setUpRecyclerRecommendation() {
        adapterRecommended = AdapterRecommended(
            requireContext(),
            listRecommendation, object : AdapterRecommended.OnItemClickListener {
                override fun onRecommendedClicked(data: ProductDetail, position: Int) {
                    val bundle = Bundle()
                    bundle.putSerializable(FragmentHome.PRODUCT_DETAIL, data)
                    val fragmentProductDetail = FragmentProductDetail()
                    fragmentProductDetail.arguments = bundle
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragmentProductDetail)
                        .addToBackStack(null).commit()
                }
            }
        )
        binding.detail2.rvRecommendation.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterRecommended
        }
        viewModelHome.getDashboardAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
//                setShimmerLayout(false)
//                hideError()
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val homeResponse =
                                Gson().fromJson<HomeResponse>(jsonObject, HomeResponse::class.java)
                            if (homeResponse.details != null) {
                                val detail = homeResponse.details
                                prepareDataForRecommended(detail.recommended)
                            }
                        } catch (e: Exception) {
                        }
                    }

                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
//                setShimmerLayout(false)
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
//                showError(it.message.toString())
                }
            }
        })
    }

    private fun prepareDataForRecommended(list: List<ProductDetail>) {
        listRecommendation.addAll(list)
        adapterRecommended.notifyDataSetChanged()
    }


    private fun initView() {
        (activity as MainActivity).showToolbar(false)
        (activity as MainActivity).showBottomNavigation(false)

        setProductImage(binding.imageView3)
        binding.btnBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        binding.btnShare.setOnClickListener {
            //share
        }

//        if (PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case), true)) {
//            binding.price.text = getString(R.string.rs) + " 5000"
//            binding.oldPrice.text = getString(R.string.rs) + " 5500"
//            binding.detail2.price.text = getString(R.string.rs) + " 5000"
//            binding.detail2.payPrice.text = getString(R.string.rs) + " 4500"
//        } else {
//            binding.price.text = getString(R.string.usd) + " 100"
//            binding.oldPrice.text = getString(R.string.usd) + " 120"
//            binding.detail2.price.text = getString(R.string.usd) + " 100"
//            binding.detail2.payPrice.text = getString(R.string.usd) + " 80"
//        }


    }

    private fun setProductImage(imageView: PercentageCropImageView) {
//        Glide.with(requireContext())
//            .load(R.drawable.splashimage)
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .transform(TopRightCropTransformation(requireContext(), 0f, 0f))
//            .into(binding.imageView3)


        imageView.cropYCenterOffsetPct = 0f
////        If you wish to have a bottom crop, call:
//        imageView.setCropYCenterOffsetPct(1.0f);
////        If you wish to have a crop 1/3 of the way down, call:
//        imageView.setCropYCenterOffsetPct(0.33f);
    }

    private fun setUpTabChoose(size: String?, width: String?, quality: Double, comfort: String?) {
        //size
        formatMyTab(binding.detail2.tabSize, size)
        //width
//        formatMyTab(binding.detail2.tabWidth, width)
        //comfort
        formatMyTab(binding.detail2.tabComfort, comfort)
        //quality
        binding.detail2.progressBarQuality.progress = Math.round(quality).toInt()
        //total reviews
        binding.detail2.tvReviewNumber.text =
            productDetail.reviews.totalReview.toString() + " REVIEWS"
        //rating
        binding.detail2.tvRatingNumber.text = productDetail.reviews.rating.toString()
        binding.detail2.ratingBar.rating = productDetail.reviews.rating.toFloat()
        binding.ratingBar1.rating = productDetail.reviews.rating.toFloat()
        //recommended
        binding.detail2.tvRecommended.text =
            productDetail.reviews.recommendedBy?.let { it.equals("")?.let { "0" } + "%" }
                ?: run { "0%" }

    }

    fun formatMyTab(tab: TabLayout, title: String?) {
        val tabStrip3 = tab.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip3.childCount) {
            tabStrip3.getChildAt(i).setOnTouchListener(OnTouchListener { v, event -> true })
            if ((tab.getTabAt(i)?.text.toString())?.equals(
                    title,
                    ignoreCase = true
                )
            ) {
                tab.getTabAt(i)?.select()
            } else {
            }
        }
    }

    private fun setUpRecyclerColor(keys: MutableSet<String>) {
        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
        }
        val listModelColor = ArrayList<ModelColor>()
        for (key in keys) {
            listModelColor.add(
                ModelColor(
                    key,
                    getImageUrlFromColorKey(key) ?: "",
                    0//not necessary
                )
            )
        }


        binding.rvColor.apply {
            layoutManager =
                flexboxLayoutManager
            adapter = AdapterColor(
                listModelColor, this@FragmentProductDetail
            )
        }
        setUpRecyclerSize()

    }

    private fun setUpFullScreen() {
        activity?.let {
            it.window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                statusBarColor = Color.TRANSPARENT
            }
        }
    }

    private fun setUpRecyclerSize() {
        adapterCircleText = AdapterCircleText(context, listText).also { adapter ->
            adapter.setCircleClickListener { data ->
                //change price according to size
                changePrice(data)
                for (item in listText) {
                    item.isSelected = item.equals(data)
                    if (item.isSelected) {
                        dynamicVarientId = data.productId
                    }
                    setCurrentVariant()
                    adapterCircleText.notifyDataSetChanged()
                    Log.d(TAG, "setUpRecyclerSize: " + item.title);
                }
            }
        }
        binding.detail2.rvSize.apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = adapterCircleText
        }

        //click color by default
        binding.rvColor.post {
            binding.rvColor.findViewHolderForAdapterPosition(0)?.itemView?.performClick();
        }

    }

    private fun changePrice(data: ModelCircleText) {

        Log.d(TAG, "changePrice:up " + data.toString());
        if (PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case), true)) {
            Log.d(TAG, "changePrice: " + data.toString());
            binding.price.text = getString(R.string.rs) + " ${data.priceNpr}"
            binding.oldPrice.text =
                getString(R.string.rs) + " ${productDetail.oldPrice}"//dont know
            binding.detail2.price.text = getString(R.string.rs) + " ${data.priceNpr}"
        } else {
            binding.price.text = getString(R.string.usd) + " ${data.priceDollar}"
            binding.oldPrice.text =
                getString(R.string.usd) + " ${productDetail.oldPrice}"//dont know
            binding.detail2.price.text = getString(R.string.usd) + " ${data.priceDollar}"
        }
    }

    private fun prepareListSize(colorHex: String) {
        listText.clear()
        val filteredVariants = getValueFromHashKey(colorHex) ?: ArrayList<Variant>()
        Log.d(TAG, "prepareListSize: " + filteredVariants);
        for (v in filteredVariants) {
            if (v.size != null) {
                binding.detail2.constraintLayout2.visibility = View.VISIBLE
                listText.add(
                    ModelCircleText(
                        v.variantId ?: 0,
                        v.size ?: "",
                        false,
                        v.price.toString(),
                        v.dollarPrice.toString()
                    )
                )

            } else {
                //disable size layout
                dynamicVarientId = productDetail.variants[0].variantId
                binding.detail2.constraintLayout2.visibility = View.GONE
            }
        }
        adapterCircleText.notifyDataSetChanged()
//        Log.d("sizechecked", "prepareListSize: 1"+listText.get(0));

        binding.detail2.rvSize.post {
            binding.detail2.rvSize.findViewHolderForAdapterPosition(0)?.itemView?.performClick();
            Log.d("sizechecked", "prepareListSize: 2" + listText);
//            setStockStatus(filteredVariants[0].quantity, 1, binding.stock)
            setStockStatus(filteredVariants[0].stockStatus, binding.stock)

        }


    }

    override fun onColorClicked(color: ModelColor, position: Int) {
        Glide.with(requireContext())
            .load(color.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(Constants.ERROR_DRAWABLE)
            .fallback(Constants.ERROR_DRAWABLE)
            .into(binding.imageView3)
        binding.imageView3.cropYCenterOffsetPct = 0f
        Log.d("testcolorclick", "onColorClicked: " + position);
        //reset data
        prepareListSize(color.colorHex)
    }

    private fun removeWishListAPI() {
        viewModel.removeFromWishAPI(
            PreferenceHandler.getToken(context).toString(),
            productDetail.productId
        )
        viewModel.getRemoveFromWishAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "removeWishListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        isProductWishList = false
                        setWishlist(false)
                        showSnackBar(
                            msg = "Product removed from wishlist",
                            Constants.GO_TO_WISHLIST
                        )
                        try {

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

    private fun addToWishListAPI() {
        Log.d(TAG, "addToWishListAPI: " + dynamicVarientId);
        viewModel.addToWishAPI(PreferenceHandler.getToken(context).toString(), dynamicVarientId)
        viewModel.getAddToWishAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "addToWishListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            showSnackBar(
                                jsonObject.get("message").toString().removeDoubleQuote(),
                                Constants.GO_TO_WISHLIST
                            )
                            isProductWishList = true
                            setWishlist(true)
                            MainActivity.NavCount.myWishlist =
                                MainActivity.NavCount.myWishlist?.plus(1)

                        } catch (e: Exception) {
                            Log.d(TAG, "addToWishListAPI:Error ${e.message}")
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
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


    private fun addToCartAPI() {
//        Toast.makeText(requireContext(), dynamicProductId.toString(), Toast.LENGTH_SHORT).show()
      if(isStockAvailable) {
          showDialogSize()
      }else{
          Toast.makeText(context,"Order out of stock",Toast.LENGTH_SHORT).show()
      }
    }

    private fun showDialogSize() {
        Log.d("sizechecked", "prepareListSize: 3" + listText);
        if (binding.detail2.constraintLayout2.isVisible) {
            val dialogBinding =
                DialogShoppingSizeBinding.inflate(LayoutInflater.from(requireContext()))
            val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
            bottomSheetDialog.setContentView(dialogBinding.root)
            dialogBinding.title.text = "Size"
            dialogBinding.btnSave.text = "Add to Cart"
//            adapterCircleText = AdapterCircleText(context, listText).also { adapter ->
//                adapter.setCircleClickListener { data ->
//                    for (item in listText) {
//                        item.isSelected = item.equals(data)
//                        if (item.isSelected) {
//                            dynamicVarientId = data.productId
//                        }
//                        adapterCircleText.notifyDataSetChanged()
//                        Log.d(TAG, "setUpRecyclerSize: " + item.title);
//                    }
//                }
//            }

            dialogBinding.recyclerView.apply {
                layoutManager = GridLayoutManager(context, 5)
                adapter = adapterCircleText
            }

            dialogBinding.btnClose.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            dialogBinding.btnSave.setOnClickListener {
//            saveSizeAPI(data, data.size)
                addCart()
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        } else {
            addCart()
        }
    }

    private fun addCart() {
//        Toast.makeText(context, dynamicVarientId.toString(), Toast.LENGTH_SHORT).show()
        viewModel.addToCartAPI(PreferenceHandler.getToken(context).toString(), dynamicVarientId)
        viewModel.getAddToCartAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "addToCartAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            isProductInCart = true
                            setCart(true)
                            showSnackBar(
                                jsonObject.get("message").toString().removeDoubleQuote(),
                                Constants.GO_TO_CART
                            )
                            MainActivity.NavCount.myBoolean =
                                MainActivity.NavCount.myBoolean?.plus(1)

                        } catch (e: Exception) {
                            Log.d(TAG, "addToCartAPI:Error ${e.message}")
                        }
                    }

                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "addToCartAPI:Error ${it.message}")
                }
            }
        })
    }

    fun setHashMapColorSize(): MutableMap<String, MutableList<Variant>> {
        //set color according to hex value
        myMaps = HashMap()
        for (item in productDetail.variants) {
            if (!myMaps.containsKey(item.colorHex)) {
                myMaps[item.colorHex] = ArrayList()
            }
            myMaps[item.colorHex]!!.add(item)
        }

        return myMaps
    }

    fun getValueFromHashKey(key: String): MutableList<Variant>? {
        myMaps.keys.forEach {
            if (key == it) {
                return myMaps[key]
            }
        }
        return null
    }

    fun getImageUrlFromColorKey(key: String): String? {
        myMaps.keys.forEach {
            if (key == it) {
                Log.d(TAG, "getImageUrlFromColorKey: " + myMaps[key]?.get(0).toString());
                return myMaps[key]?.get(0)?.imageUrl
            }
        }
        return ""
    }

    fun setCurrentVariant() {
        if (dynamicVarientId != 0) {
            Log.d("testvarientid", "setCurrentVariant: " + dynamicVarientId);
            val myCurrentVarient = productDetail.variants.filter {
                it.variantId == dynamicVarientId
            }.single()

            isProductInCart = myCurrentVarient.isInCart
            isProductWishList = myCurrentVarient.isInWishlist
            setWishlist(isProductWishList)
            setCart(isProductInCart)
            setStockStatus(myCurrentVarient.stockStatus, binding.stock)
        }
    }

    fun setStockStatus(
        stock: Int,
        myQuantity: Int,
        tv_stock: TextView,
        STOCKLIMIT: Int = 6
    ): String {
        var textToDisplay = ""
        if (myQuantity >= stock) {
            textToDisplay = "Out of Stock"
            changeColor(tv_stock, R.color.colorRedDark, R.color.white, requireContext())
        } else if (myQuantity < stock) {
            if ((stock - myQuantity) < STOCKLIMIT) {
                changeColor(tv_stock, R.color.colorYellowDark, R.color.white, requireContext())
                textToDisplay = "${stock - myQuantity} item(s) remaining"
            } else {
                textToDisplay = "In stock"
                changeColor(tv_stock, R.color.colorGreenDark, R.color.white, requireContext())
            }

        }
        return textToDisplay
    }

    fun setStockStatus(stock: String, tv_stock: TextView): String {

        var textToDisplay = stock
        if (stock.contains("Out of Stock", ignoreCase = true)) {
            //TOD
                isStockAvailable=false
            binding.imageCart.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),R.color.colorGray));

            binding.detail2.ivCart.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorGray
                )
            );
            changeColor(tv_stock, R.color.colorRedDark, R.color.white, requireContext())
        } else if (stock.contains("In stock", ignoreCase = true)) {
            binding.imageCart.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),R.color.colorBlack));

            isStockAvailable=true
            binding.detail2.ivCart.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorBlack
                )
            );
            changeColor(tv_stock, R.color.colorGreenDark, R.color.white, requireContext())

        } else {
            isStockAvailable=true
            binding.imageCart.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),R.color.colorBlack));

            binding.detail2.ivCart.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorBlack
                )
            );

            changeColor(tv_stock, R.color.colorYellowDark, R.color.white, requireContext())
        }
        tv_stock.setText(textToDisplay)
        return textToDisplay
    }

    private fun changeColor(stock: TextView, colorLight: Int, colorDark: Int, context: Context) {
        stock.setTextColor(ContextCompat.getColor(context, colorDark));
        stock.background.setTint(ContextCompat.getColor(context, colorLight));
    }


}