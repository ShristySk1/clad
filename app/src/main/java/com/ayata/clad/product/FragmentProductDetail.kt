package com.ayata.clad.product

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
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
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.home.response.Variant
import com.ayata.clad.product.adapter.AdapterColor
import com.ayata.clad.product.adapter.AdapterRecommendation
import com.ayata.clad.product.viewmodel.ProductViewModel
import com.ayata.clad.product.viewmodel.ProductViewModelFactory
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.model.ModelCircleText
import com.ayata.clad.utils.PercentageCropImageView
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.copyToClipboard
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
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
    var galleryBundle: Bundle? = null

    //    var dynamicProductId = 0
    var dynamicVarientId = 0
    var choosenSizePosition = 0

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
        setUpTabChoose()
        tapToCopyListener()
        productLikedListener()
        setUpRecyclerRecommendation()
        binding.imageView3.setOnClickListener {
            goToGalleryView()
            true
        }

        return binding.root
    }

    private fun goToGalleryView() {
        val fragment = FragmentProductDetailFull2()
        fragment.arguments = galleryBundle
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
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
                setProductData()
            }
            galleryBundle = bundle

        } else {
            Log.d(TAG, "getBundle:null ");
        }
    }

    private fun setProductData() {
        choosenSizePosition = 0
        Log.d(TAG, "setProductData: " + dynamicVarientId);
        if (PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case), true)) {
            binding.price.text = getString(R.string.rs) + " ${productDetail.price}"
            binding.oldPrice.text = getString(R.string.rs) + " ${productDetail.oldPrice}"
            binding.detail2.price.text = getString(R.string.rs) + " ${productDetail.price}"

        } else {
            binding.price.text = getString(R.string.usd) + " ${productDetail.price}"
            binding.oldPrice.text = getString(R.string.usd) + " ${productDetail.oldPrice}"
            binding.detail2.price.text = getString(R.string.usd) + " ${productDetail.price}"
        }
        if(productDetail.isCouponAvailable){
            binding.detail2.constraintLayout.visibility=View.VISIBLE
            binding.detail2.couponTitle.text =productDetail.coupon.title
            binding.detail2.couponDesc.text =productDetail.coupon.description
            binding.detail2.tvTextToCopy.text =productDetail.coupon.code//code

        }else{
            binding.detail2.constraintLayout.visibility=View.GONE
        }
        binding.name.text = productDetail.name
        binding.storeName.text = productDetail.vendor
        binding.description.text = Html.fromHtml(productDetail.description)
        binding.detail2.name.text = productDetail.name
        isProductWishList = productDetail.isInWishlist
        isProductInCart = productDetail.isInCart
//        Glide.with(requireContext()).load(productDetail.image_url).into(binding.imageView3)
        setWishlist(isProductWishList)
        setCart(isProductInCart)
        val colorsize = setHashMapColorSize()
        setUpRecyclerColor(colorsize.keys)
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            ProductViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[ProductViewModel::class.java]
    }

    private fun productLikedListener() {
        binding.cardWish.setOnClickListener {
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
        binding.cardCart.setOnClickListener {
            if (this::productDetail.isInitialized) {
                if (isProductInCart) {
                    showSnackBar("Product already added in cart")
                } else {
                    if (PreferenceHandler.getToken(context) != "")
                        addToCartAPI() else (activity as MainActivity).showDialogLogin()
//                    addToCartAPI()
                }
//                setCart()
//                isProductInCart=(!isProductInCart)
            }

        }

        binding.detail2.ivCart.setOnClickListener {
            if (this::productDetail.isInitialized) {
                if (isProductInCart) {
                    showSnackBar("Product already added in cart")
                } else {
                    if (PreferenceHandler.getToken(context) != "")
                        addToCartAPI() else (activity as MainActivity).showDialogLogin()
                }
//                setCart()
//                isProductInCart=(!isProductInCart)
            }
        }
    }

    private fun showSnackBar(msg: String) {
        val snackbar = Snackbar
            .make(binding.root, msg, Snackbar.LENGTH_SHORT)
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.show()
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
        binding.detail2.rvRecommendation.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = AdapterRecommendation(
                requireContext(),
                prepareDataForRecommended(mutableListOf()).toList()
            ).also {
                it.setProductClickListener { recommendedProduct ->
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, FragmentProductDetail())
                        .addToBackStack(null).commit()
                }
            }
        }

    }

    private fun prepareDataForRecommended(listRecommended: MutableList<ModelRecommendedProduct>): MutableList<ModelRecommendedProduct> {
        listRecommended.clear()
        listRecommended.add(
            ModelRecommendedProduct(
                "https://freepngimg.com/thumb/categories/627.png",
                "Nike ISPA Overreact Sail Multi", "Nike Company",
                "https://p7.hiclipart.com/preview/595/571/731/swoosh-nike-logo-just-do-it-adidas-nike.jpg",
                "3561", "555"
            )
        )
        listRecommended.add(
            ModelRecommendedProduct(
                "https://images.squarespace-cdn.com/content/v1/566e100d0e4c116bdc11b2c2/1473302788755-FL48S6YFWHYC9KU18K52/245282-ceb4145ac7b646889a16b6f5dbd2f455.png?format=750w",
                "adidas Yeezy Boost 700 MNVN Bone",
                "Lowest Ask",
                "https://www.pngkit.com/png/full/436-4366026_adidas-stripes-png-adidas-logo-without-name.png",
                "7589", "550"
            )
        )
        listRecommended.add(
            ModelRecommendedProduct(
                "https://www.pngkit.com/png/full/70-704028_running-shoes-png-image-running-shoes-clipart-transparent.png",
                "Jordan 11 Retro Low White Concord (W) ",
                "Lowest Ask",
                "https://upload.wikimedia.org/wikipedia/en/thumb/3/37/Jumpman_logo.svg/1200px-Jumpman_logo.svg.png",
                "4555", "458"
            )
        )
        return listRecommended

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

    private fun setUpTabChoose() {
        val tabStrip = binding.detail2.tab.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).setOnTouchListener(OnTouchListener { v, event -> true })
        }
        binding.detail2.tab.getTabAt(1)?.select()
        val tabStrip2 = binding.detail2.tab2.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            tabStrip2.getChildAt(i).setOnTouchListener(OnTouchListener { v, event -> true })
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
                binding.detail2.constraintLayout2.visibility = View.GONE
            }
        }
        adapterCircleText.notifyDataSetChanged()
        binding.detail2.rvSize.post {
            binding.detail2.rvSize.findViewHolderForAdapterPosition(0)?.itemView?.performClick();
        }

    }

    override fun onColorClicked(color: ModelColor, position: Int) {
        Glide.with(requireContext())
            .load(color.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.splashimage)
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
                        showSnackBar(msg = "Product removed from wishlist")
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
                        showSnackBar("Product added to wishlist")
                        isProductWishList = true
                        setWishlist(true)
                        MainActivity.NavCount.myWishlist = MainActivity.NavCount.myWishlist?.plus(1)

                        try {

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


    private fun addToCartAPI() {
//        Toast.makeText(requireContext(), dynamicProductId.toString(), Toast.LENGTH_SHORT).show()
        Log.d(TAG, "addToCartAPI: " + dynamicVarientId);
        showDialogSize()
    }

    private fun showDialogSize() {
        if (binding.detail2.constraintLayout2.isVisible) {
            val dialogBinding =
                DialogShoppingSizeBinding.inflate(LayoutInflater.from(requireContext()))
            val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
            bottomSheetDialog.setContentView(dialogBinding.root)
            dialogBinding.title.text = "Size"
            dialogBinding.btnSave.text = "Add to Cart"
            adapterCircleText = AdapterCircleText(context, listText).also { adapter ->
                adapter.setCircleClickListener { data ->
                    for (item in listText) {
                        item.isSelected = item.equals(data)
                        if (item.isSelected) {
                            dynamicVarientId = data.productId
                        }
                        adapterCircleText.notifyDataSetChanged()
                        Log.d(TAG, "setUpRecyclerSize: " + item.title);
                    }
                }
            }

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
        viewModel.addToCartAPI(PreferenceHandler.getToken(context).toString(), dynamicVarientId)
        viewModel.getAddToCartAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "addToCartAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        isProductInCart = true
                        setCart(true)
                        showSnackBar("Product added to cart")
                        MainActivity.NavCount.myBoolean = MainActivity.NavCount.myBoolean?.plus(1)
                        try {

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

}