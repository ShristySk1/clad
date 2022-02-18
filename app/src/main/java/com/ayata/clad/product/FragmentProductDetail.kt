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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentProductDetailBinding
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.home.response.HomeResponse
import com.ayata.clad.home.response.ProductDetail
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
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson


class FragmentProductDetail : Fragment(),AdapterColor.OnItemClickListener {
    private val TAG = "FragmentProductDetail"
    private lateinit var adapterCircleText: AdapterCircleText
    private lateinit var binding: FragmentProductDetailBinding
    private var listText = ArrayList<ModelCircleText>()
    private var isProductWishList: Boolean = false
    private var isProductInCart:Boolean = false

    private lateinit var viewModel:ProductViewModel
    private lateinit var productDetail: ProductDetail

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
        setUpRecyclerColor()
        setUpRecyclerSize()
        setUpTabChoose()
        tapToCopyListener()
        productLikedListener()
        setUpRecyclerRecommendation()
        return binding.root
    }

    private fun getBundle(){
        val bundle=arguments
        if(bundle!=null){
            val data=bundle.getSerializable(FragmentHome.PRODUCT_DETAIL)
            if(data!=null){
                productDetail=data as ProductDetail
                setProductData()
            }
        }
    }

    private fun setProductData(){
        if(PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case),true)){
            binding.price.text=getString(R.string.rs)+" ${productDetail.price}"
            binding.oldPrice.text=getString(R.string.rs)+" ${productDetail.old_price}"
            binding.detail2.price.text=getString(R.string.rs)+" ${productDetail.price}"
            binding.detail2.payPrice.text=getString(R.string.rs)+" 4500"
        }else{
            binding.price.text=getString(R.string.usd)+" ${productDetail.price}"
            binding.oldPrice.text=getString(R.string.usd)+" ${productDetail.old_price}"
            binding.detail2.price.text=getString(R.string.usd)+" ${productDetail.price}"
            binding.detail2.payPrice.text=getString(R.string.usd)+" 80"
        }
        binding.name.text=productDetail.name
        binding.storeName.text=productDetail.owner
        binding.description.text=Html.fromHtml(productDetail.description)
        binding.detail2.name.text=productDetail.name
        isProductWishList=productDetail.is_in_wishlist
        isProductInCart=productDetail.is_in_cart
        setWishlist(isProductWishList)
        setCart(isProductInCart)
    }

    private fun setUpViewModel(){
        viewModel=ViewModelProvider(this,
            ProductViewModelFactory(ApiRepository(ApiService.getInstance())))[ProductViewModel::class.java]
    }
    private fun productLikedListener() {
        binding.cardWish.setOnClickListener {
            if(this::productDetail.isInitialized){
                if(isProductWishList){
                    removeWishListAPI()
                }else{
                    addToWishListAPI()
                }
//                setWishlist()
//                isProductWishList=(!isProductWishList)
            }

        }
        binding.cardCart.setOnClickListener {
            if(this::productDetail.isInitialized){
                if(isProductInCart){
                    removeCartAPI()
                }else{
                    addToCartAPI()
                }
//                setCart()
//                isProductInCart=(!isProductInCart)
            }

        }

        binding.detail2.ivCart.setOnClickListener {
            if(this::productDetail.isInitialized){
                if(isProductInCart){
                    removeCartAPI()
                }else{
                    addToCartAPI()
                }
//                setCart()
//                isProductInCart=(!isProductInCart)
            }
        }
    }

    private fun showSnackBar(msg:String){
        val snackbar = Snackbar
            .make(binding.root, msg, Snackbar.LENGTH_SHORT)
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun setWishlist(isWishList:Boolean){
        //from api
        if (isWishList) {
            binding.ivHeart.setImageResource(R.drawable.ic_heart_filled)
        } else {
            binding.ivHeart.setImageResource(R.drawable.ic_heart_outline)
        }
    }

    private fun setCart(isCart:Boolean){
        //from api
        if(isCart){
            binding.imageCart.setImageResource(R.drawable.ic_bag_filled)
            binding.detail2.ivCart.setImageResource(R.drawable.ic_bag_filled)
        }else{
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
            adapter = AdapterRecommendation(requireContext(),
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
                "3561","555"
            )
        )
        listRecommended.add(
            ModelRecommendedProduct(
                "https://images.squarespace-cdn.com/content/v1/566e100d0e4c116bdc11b2c2/1473302788755-FL48S6YFWHYC9KU18K52/245282-ceb4145ac7b646889a16b6f5dbd2f455.png?format=750w",
                "adidas Yeezy Boost 700 MNVN Bone",
                "Lowest Ask",
                "https://www.pngkit.com/png/full/436-4366026_adidas-stripes-png-adidas-logo-without-name.png",
                "7589","550"
            )
        )
        listRecommended.add(
            ModelRecommendedProduct(
                "https://www.pngkit.com/png/full/70-704028_running-shoes-png-image-running-shoes-clipart-transparent.png",
                "Jordan 11 Retro Low White Concord (W) ",
                "Lowest Ask",
                "https://upload.wikimedia.org/wikipedia/en/thumb/3/37/Jumpman_logo.svg/1200px-Jumpman_logo.svg.png",
                "4555","458"
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

        if(PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case),true)){
            binding.price.text=getString(R.string.rs)+" 5000"
            binding.oldPrice.text=getString(R.string.rs)+" 5500"
            binding.detail2.price.text=getString(R.string.rs)+" 5000"
            binding.detail2.payPrice.text=getString(R.string.rs)+" 4500"
        }else{
            binding.price.text=getString(R.string.usd)+" 100"
            binding.oldPrice.text=getString(R.string.usd)+" 120"
            binding.detail2.price.text=getString(R.string.usd)+" 100"
            binding.detail2.payPrice.text=getString(R.string.usd)+" 80"
        }


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

    private fun setUpRecyclerColor() {
        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
        }
        binding.rvColor.apply {
            layoutManager =
                flexboxLayoutManager
            adapter = AdapterColor(
                listOf(
                    R.color.color1,
                    R.color.color2,
                    R.color.color3,
                    R.color.color4
                ),this@FragmentProductDetail
            )
        }
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
                for (item in listText) {
                    item.isSelected = item.equals(data)
                    adapterCircleText.notifyDataSetChanged()
                    Log.d(TAG, "setUpRecyclerSize: " + item.title);

                }

            }
        }
        prepareListSize()

        binding.detail2.rvSize.apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = adapterCircleText
        }

    }

    private fun prepareListSize() {
        listText.clear()
        listText.add(ModelCircleText("xs", false))
        listText.add(ModelCircleText("s", true))
        listText.add(ModelCircleText("m", false))
        listText.add(ModelCircleText("l", false))
        listText.add(ModelCircleText("xl", false))
        adapterCircleText.notifyDataSetChanged()
    }

    override fun onColorClicked(color: Int,position:Int) {
        //change image
        val url=when (position) {
            2 -> {
               "https://pa.namshicdn.com/product/A6/20076W/1-zoom-desktop.jpg"
            }
            1 -> {
                "https://m.media-amazon.com/images/I/71hNVecVSrL._AC_UX342_.jpg"
            }
            3->{
                R.drawable.example_img
            }
            else -> {
                ""
            }
        }
        Glide.with(requireContext())
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.splashimage)
            .into(binding.imageView3)
        binding.imageView3.cropYCenterOffsetPct = 0f
    }

    private fun removeWishListAPI(){
        viewModel.removeFromWishAPI(PreferenceHandler.getToken(context).toString(),productDetail.id)
        viewModel.getRemoveFromWishAPI().observe(viewLifecycleOwner,{
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "removeWishListAPI: ${it.data}")
                    val jsonObject=it.data
                    if(jsonObject!=null){
                        isProductWishList=false
                        setWishlist(false)
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

    private fun addToWishListAPI(){
        viewModel.addToWishAPI(PreferenceHandler.getToken(context).toString(),productDetail.id)
        viewModel.getAddToWishAPI().observe(viewLifecycleOwner,{
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "addToWishListAPI: ${it.data}")
                    val jsonObject=it.data
                    if(jsonObject!=null){
                        showSnackBar("Product added to wishlist")
                        isProductWishList=true
                        setWishlist(true)
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

    private fun removeCartAPI(){
        viewModel.removeFromCartAPI(PreferenceHandler.getToken(context).toString(),productDetail.id)
        viewModel.getRemoveFromCartAPI().observe(viewLifecycleOwner,{
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "removeCartAPI: ${it.data}")
                    val jsonObject=it.data
                    if(jsonObject!=null){
                        isProductInCart=false
                        setCart(false)
                        showSnackBar("Product removed from cart")
                        try{

                        }catch (e:Exception){
                            Log.d(TAG, "removeCartAPI:Error ${e.message}")
                        }
                    }

                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "removeCartAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun addToCartAPI(){
        viewModel.addToCartAPI(PreferenceHandler.getToken(context).toString(),productDetail.id)
        viewModel.getAddToCartAPI().observe(viewLifecycleOwner,{
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "addToCartAPI: ${it.data}")
                    val jsonObject=it.data
                    if(jsonObject!=null){
                        isProductInCart=true
                        setCart(true)
                        showSnackBar("Product added to cart")
                        try{

                        }catch (e:Exception){
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

}