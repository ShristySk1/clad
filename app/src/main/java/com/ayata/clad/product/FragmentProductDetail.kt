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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.CreateLinkViewModel
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
import com.ayata.clad.home.response.Reviews
import com.ayata.clad.home.response.Variant
import com.ayata.clad.home.viewmodel.HomeViewModel
import com.ayata.clad.home.viewmodel.HomeViewModelFactory
import com.ayata.clad.product.adapter.AdapterColor
import com.ayata.clad.product.adapter.AdapterProduct
import com.ayata.clad.product.adapter.AdapterQaMultiple
import com.ayata.clad.product.qa.FragmentQA
import com.ayata.clad.product.qa.ModelQA
import com.ayata.clad.product.reviews.FragmentReview
import com.ayata.clad.product.viewmodel.ProductViewModel
import com.ayata.clad.product.viewmodel.ProductViewModelFactory
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.model.ModelCircleText
import com.ayata.clad.utils.*
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.noowenz.showmoreless.ShowMoreLess
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import java.io.Serializable
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class FragmentProductDetail : Fragment(), AdapterColor.OnItemClickListener {
    private lateinit var myQAList: MutableList<ModelQA>
    private val TAG = "FragmentProductDetail"
    private lateinit var adapterCircleText: AdapterCircleText
    private lateinit var binding: FragmentProductDetailBinding
    private var listText = ArrayList<ModelCircleText>()
    private var isProductWishList: Boolean = false
    private var isProductInCart: Boolean = false
    private lateinit var viewModel: ProductViewModel
    private lateinit var productDetail: ProductDetail
    var galleryBundle = listOf<String>()
    private var listRecommendation = ArrayList<ProductDetail>()
    private lateinit var progressDialog: ProgressDialog


    private lateinit var adapterRecommended: AdapterRecommended
    private lateinit var viewModelHome: HomeViewModel
    var dynamicVarientId = 0
    var choosenSizePosition = 0
    var isStockAvailable = true
    var makeMainLayoutVisible = true
    val MAX_TEXT_CHARACTER = 200
    val MAX_TEXT_LINES = 5
    private val createLinkViewModel by lazy {
        ViewModelProvider(this).get(CreateLinkViewModel::class.java)
    }

    //for color and size
    lateinit var myMaps: MutableMap<String, MutableList<Variant>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        getBundle()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        initView()
        observeProductApi()
        if (this::productDetail.isInitialized) {
            Log.d("testproduct", "onCreateView: " + productDetail);
            setShimmerLayout(false)
            setProductData()
        }
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
//                    "http://192.168.1.67:3000/product/12/aamayra-kurtha"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "Share Using"))
            } catch (e: Exception) {
                Log.d(TAG, "onCreateView: " + e.message.toString());
            }

            //test
//            createLinkViewModel.refferCode.value=productDetail.productId
//            createLinkViewModel.onCreateLinkClick()
//            val intent = Intent(Intent.ACTION_SEND)
//            intent.type = "text/plain"
//            intent.putExtra(Intent.EXTRA_TEXT, createLinkViewModel.shortLink.value)
//            startActivity(Intent.createChooser(intent, "Share Product"))

        }
        binding.imageSlider.setOnClickListener {
            goToGalleryView()
        }
        binding.cardGallary.setOnClickListener {
            goToGalleryView()
        }
        return binding.root
    }

    private fun setUpRecyclerQA() {

        binding.detail2.askQA.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.main_fragment,
                    FragmentQA.newInstance(
                        productDetail.productId,
                        productDetail.brand?.name ?: productDetail.vendor
                    )
                )
                .addToBackStack(null).commit()
        }
        binding.detail2.tvViewAllQuestion.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.main_fragment,
                    FragmentQA.newInstance(
                        productDetail.productId,
                        productDetail.brand?.name ?: productDetail.vendor
                    )
                )
                .addToBackStack(null).commit()
        }
//        myQAList = mutableListOf(
//
//            ModelQA.Question("Which fabric is this?","10th June 2022","Sita"),
//            ModelQA.Divider(),
//            ModelQA.Question("Is this available in black color as well?","9th June 2022","Nina"),
//            ModelQA.Answer("Sorry. Not available","9th June 2022","GoldStar"),
//            )
        myQAList = mutableListOf()
        productDetail.queries?.let {
            if (it.size > 0) {
                binding.detail2.groupQuestion.visibility = View.VISIBLE
                binding.detail2.textQaEmpty.visibility = View.GONE
                productDetail.queries!!.forEach {
                    myQAList.add(
                        ModelQA.Question(
                            it.question,
                            it.postedAt,
                            it.postedBy,
                            false,
                            false,
                            it.questionId
                        )
                    )
                    if (it.answer.isNotEmpty()) {
                        myQAList.add(
                            ModelQA.Answer(
                                it.answer,
                                it.repliedAt,
                                productDetail.brand?.name ?: productDetail.vendor
                            )
                        )
                    }
                    myQAList.add(ModelQA.Divider())
                }
            } else {
                binding.detail2.groupQuestion.visibility = View.GONE
                binding.detail2.textQaEmpty.visibility = View.VISIBLE
            }
        } ?: kotlin.run {
            binding.detail2.groupQuestion.visibility = View.GONE
            binding.detail2.textQaEmpty.visibility = View.VISIBLE
        }
        //we need to show with both question and answer and also only one
//        var myfirstQuestionAnswer = mutableListOf<ModelQA>()
//        if (myQAList.size > 0) {
//            for (model in myQAList) {
//                if (model is ModelQA.Answer) {
//                    if (model.answer.isEmpty()) {
//                        Log.d("ttestquestion", "setUpRecyclerQA: empty");
//                    } else {
//                        Log.d("ttestquestion", "setUpRecyclerQA: added 2");
//                        myfirstQuestionAnswer.add(model)
//                        break
//                    }
//                } else if (model is ModelQA.Question){
//                    Log.d("ttestquestion", "setUpRecyclerQA:added 1 ");
//                    myfirstQuestionAnswer.clear()
//                    myfirstQuestionAnswer.add(model)
//                }
//            }
//            if(myfirstQuestionAnswer.size>0){
//                binding.detail2.groupQuestion.visibility = View.VISIBLE
//            }else{
//                binding.detail2.groupQuestion.visibility = View.GONE
//            }
//        } else {
//            binding.detail2.groupQuestion.visibility = View.GONE
//        }
//        binding.detail2.textQuestionTopic.text =
//            String.format(
//                requireContext().getString(com.ayata.clad.R.string.questions_about_this_product_5),
//                1,
//                1
//            )
        binding.detail2.recyclerQa.apply {
            layoutManager = LinearLayoutManager(requireContext())
            AdapterQaMultiple(myQAList).also { adapter = it }.also { it.setMyItems(myQAList) }
        }


    }

    private fun observeProductApi() {
        viewModel.getProductAPI().observeOnceAfterInit(viewLifecycleOwner, {
            if (it != null) {
                Log.d("testproocutapi", "observeProductApi: " + it.status);
                when (it.status) {
                    Status.SUCCESS -> {
                        setShimmerLayout(false)
                        val jsonObject = it.data
                        if (jsonObject != null) {
                            try {
                                val jsonProducts = jsonObject.get("details").asJsonArray
                                val type: Type =
                                    object : TypeToken<ArrayList<ProductDetail?>?>() {}.type
                                val list: ArrayList<ProductDetail> = Gson().fromJson(
                                    jsonProducts,
                                    type
                                )
                                productDetail = list.first()
                                setProductData()
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    Status.LOADING -> {
                        setShimmerLayout(true)
                    }
                    Status.ERROR -> {
                        setShimmerLayout(false)
                        //Handle Error
                        binding.mainLayout.visibility = View.GONE
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        Log.d(TAG, "addToCartAPI:Error ${it.message}")
                    }
                }
            }


        })
    }

    private fun goToGalleryView() {
        val fragment = FragmentProductDetailFull2()
        val bundle = Bundle()
        bundle.putSerializable("gallary", galleryBundle as Serializable)
        bundle.putString("title", productDetail.name)
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
            val id = bundle.getInt(FragmentHome.PRODUCT_DETAIL_ID)
            Log.d(TAG, "getBundle: " + id);
            if (id != 0) {
                viewModel.productAPI(PreferenceHandler.getToken(context)!!, id)
            } else {
                val data = bundle.getSerializable(FragmentHome.PRODUCT_DETAIL) as ProductDetail
                if (data != null) {
                    //bundle
                    // productDetail = data as ProductDetail
                    //setProductData()
                    //api
                    viewModel.productAPI(PreferenceHandler.getToken(context)!!, data.productId)
                } else {
                    makeMainLayoutVisible = false
                }
            }

        } else {
            Log.d(TAG, "getBundle:null ");
            makeMainLayoutVisible = false
        }
    }

    private fun setProductData() {
        choosenSizePosition = 0
//        if (PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case), true)) {
//            binding.price.text = getString(R.string.rs) + " ${productDetail.price}"
//            binding.oldPrice.text = getString(R.string.rs) + " ${productDetail.oldPrice}"
//            binding.detail2.price.text = getString(R.string.rs) + " ${productDetail.price}"
//
//        } else {
//            binding.price.text = getString(R.string.usd) + " ${productDetail.dollar_price}"
//            binding.oldPrice.text = getString(R.string.usd) + " ${productDetail.old_dollar_price}"
//            binding.detail2.price.text = getString(R.string.usd) + " ${productDetail.dollar_price}"
//        }
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
        Log.d("testdescx", "setProductData: "+productDetail.description);
        if (productDetail.description.isEmpty()) {
            hideDesc()

        } else {
            showDesc(productDetail.description)
        }

        if (productDetail.description.length < MAX_TEXT_CHARACTER) {

        } else {
            initViews()
        }

        binding.detail2.name.text = productDetail.name
//        isProductWishList = productDetail.isInWishlist
//        isProductInCart = productDetail.isInCart
//        Glide.with(requireContext()).load(productDetail.image_url).into(binding.imageView3)
        //reviews
        if (productDetail.reviews != null) {
            binding.detail2.linearLayout5.visibility = View.VISIBLE
            setUpTabChoose(
                productDetail.reviews!!.size,
                productDetail.reviews!!.width,
                productDetail.reviews!!.quality,
                productDetail.reviews!!.comfort
            )
        } else {
            binding.detail2.linearLayout5.visibility = View.GONE
        }
        val colorsize = setHashMapColorSize()
        setUpRecyclerColor(colorsize.keys)
//        setCurrentVariant()

        binding.detail2.tvViewAllReview.setOnClickListener {
            val fragment = FragmentReview.newInstance(
                productDetail.reviews ?: Reviews(
                    null,
                    0.0,
                    0.0,
                    "0",
                    null,
                    0,
                    null,
                    null
                )
            )
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                .addToBackStack(null)
                .commit()
        }
        setUpRecyclerQA()
    }

    fun showDesc(desc: String) {
        binding.detail2.tvDesc.visibility = View.VISIBLE
        binding.detail2.description.visibility=View.VISIBLE
        binding.detail2.description.text = Html.fromHtml(desc)
    }

    fun hideDesc() {
        binding.detail2.tvDesc.visibility = View.GONE
        binding.detail2.description.visibility = View.GONE
    }

    private fun initViews() {
        ShowMoreLess.Builder(requireContext())
            /*.textLengthAndLengthType(
                    length = 100,
                    textLengthType = ShowMoreLess.TYPE_CHARACTER
            )*/
            .textLengthAndLengthType(
                length = MAX_TEXT_LINES,
                textLengthType = ShowMoreLess.TYPE_LINE
            )
            .showMoreLabel("See more")
            .showLessLabel("See less")
            .showMoreLabelColor(Color.GRAY)
            .showLessLabelColor(Color.GRAY)
            .labelUnderLine(labelUnderLine = true)
            .labelBold(labelBold = true)
            .expandAnimation(expandAnimation = true)
            .enableLinkify(linkify = false)
            .textClickable(
                textClickableInExpand = true,
                textClickableInCollapse = true
            )
            .build().apply {
                addShowMoreLess(
                    textView = binding.detail2.description,
                    text = binding.detail2.description.text,
                    isContentExpanded = false
                )
                setListener(object : ShowMoreLess.OnShowMoreLessClickedListener {
                    override fun onShowMoreClicked() {
                        //We can handle or save show more state
                    }

                    override fun onShowLessClicked() {
                        //We can handle or save show less state
                    }
                })
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
            productDetail.variants.filter { dynamicVarientId == it.variantId }
                .single().isInWishlist = true
        } else {
            binding.ivHeart.setImageResource(R.drawable.ic_heart_outline)
            productDetail.variants.filter { dynamicVarientId == it.variantId }
                .single().isInWishlist = false
        }
    }

    private fun setCart(isCart: Boolean) {
        //from api
        if (isCart) {
            binding.imageCart.setImageResource(R.drawable.ic_bag_filled)
            binding.detail2.ivCart.setImageResource(R.drawable.ic_bag_filled)
            productDetail.variants.filter { dynamicVarientId == it.variantId }.single().isInCart =
                true
        } else {
            binding.imageCart.setImageResource(R.drawable.ic_cart)
            binding.detail2.ivCart.setImageResource(R.drawable.ic_cart)
            productDetail.variants.filter { dynamicVarientId == it.variantId }.single().isInCart =
                false
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
//        setProductImage(binding.imageSlider)
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

        if (!makeMainLayoutVisible) {
            binding.mainLayout.visibility = View.GONE
        }
    }

//    private fun setProductImage(imageView: Sl) {
//        Glide.with(requireContext())
//            .load(R.drawable.splashimage)
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .transform(TopRightCropTransformation(requireContext(), 0f, 0f))
//            .into(binding.imageView3)


//        imageView.cropYCenterOffsetPct = 0f
////        If you wish to have a bottom crop, call:
//        imageView.setCropYCenterOffsetPct(1.0f);
////        If you wish to have a crop 1/3 of the way down, call:
//        imageView.setCropYCenterOffsetPct(0.33f);
//    }

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
            productDetail.reviews?.totalReview.toString() + " REVIEWS"
        //rating
        binding.detail2.tvRatingNumber.text = productDetail.reviews?.rating.toString()
        binding.detail2.ratingBar.rating = productDetail.reviews?.rating!!.toFloat()
        binding.ratingBar1.rating = productDetail.reviews?.rating!!.toFloat()
        //recommended
        binding.detail2.tvRecommended.text =
            productDetail.reviews?.recommendedBy?.let { it.equals("")?.let { "0" } + "%" }
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
//                ModelColor(
//                    key,
//                    getImageUrlFromColorKey(key) ?: "",
//                    0//not necessary
//                )

                ModelColor(
                    key,
                    galleryBundle,
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
//        activity?.let {
//            it.window.apply {
//                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                statusBarColor = Color.TRANSPARENT
//            }
//        }
    }

    private fun setUpRecyclerSize() {
        adapterCircleText = AdapterCircleText(context, listText).also { adapter ->
            adapter.setCircleClickListener { data ->
                //change price according to size
                for (item in listText) {
                    item.isSelected = item.equals(data)
                    if (item.isSelected) {
                        dynamicVarientId = data.productId
                    }

                    Log.d(TAG, "setUpRecyclerSize: " + item.title);
                }
                changePrice(data)
                setCurrentVariant()
                adapterCircleText.notifyDataSetChanged()
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
//        Glide.with(requireContext())
//            .load(color.imageUrl)
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .error(Constants.ERROR_DRAWABLE)
//            .fallback(Constants.ERROR_DRAWABLE)
//            .into(binding.imageView3)


//        binding.imageView3.cropYCenterOffsetPct = 0f
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
        binding.imageSlider.isClickable = false
        viewModel.addToWishAPI(PreferenceHandler.getToken(context).toString(), dynamicVarientId)
        viewModel.getAddToWishAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {

                    progressDialog.dismiss()
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
                            binding.imageSlider.isClickable = true
                            MainActivity.NavCount.myWishlist =
                                MainActivity.NavCount.myWishlist?.plus(1)

                        } catch (e: Exception) {
                            binding.imageSlider.isClickable = true
                            Log.d(TAG, "addToWishListAPI:Error ${e.message}")
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        binding.imageSlider.isClickable = true
                    }

                }
                Status.LOADING -> {
                    progressDialog = ProgressDialog.newInstance("", "")
                    val prev: Fragment? =
                        parentFragmentManager.findFragmentByTag("like_progress")
                    if (prev != null) {
                        val df: DialogFragment = prev as DialogFragment
                        df.dismiss()
                    }
                    progressDialog.show(parentFragmentManager, "like_progress")
                }
                Status.ERROR -> {
                    progressDialog.dismiss()
                    binding.imageSlider.isClickable = true
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "addToWishListAPI:Error ${it.message}")
                }
            }
        })
    }


    private fun addToCartAPI() {
//        Toast.makeText(requireContext(), dynamicProductId.toString(), Toast.LENGTH_SHORT).show()
        if (isStockAvailable) {
            showDialogSize()
        } else {
            Toast.makeText(context, "Order out of stock", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDialogSize() {
//        Log.d("sizechecked", "prepareListSize: 3" + listText);
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
        binding.imageSlider.isClickable = false
        viewModel.addToCartAPI(PreferenceHandler.getToken(context).toString(), dynamicVarientId)
        viewModel.getAddToCartAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dismiss()
                    Log.d(TAG, "addToCartAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            isProductInCart = true
                            setCart(true)
                            binding.imageSlider.isClickable = true
                            showSnackBar(
                                jsonObject.get("message").toString().removeDoubleQuote(),
                                Constants.GO_TO_CART
                            )
                            MainActivity.NavCount.myBoolean =
                                MainActivity.NavCount.myBoolean?.plus(1)

                        } catch (e: Exception) {
                            binding.imageSlider.isClickable = true
                            Log.d(TAG, "addToCartAPI:Error ${e.message}")
                        }
                    } else {
                        binding.imageSlider.isClickable = true
                    }

                }
                Status.LOADING -> {
                    progressDialog = ProgressDialog.newInstance("", "")
                    val prev: Fragment? =
                        parentFragmentManager.findFragmentByTag("add_progress")
                    if (prev != null) {
                        val df: DialogFragment = prev as DialogFragment
                        df.dismiss()
                    }
                    progressDialog.show(parentFragmentManager, "add_progress")
                }
                Status.ERROR -> {
                    progressDialog.dismiss()
                    binding.imageSlider.isClickable = true
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
                return myMaps[key]?.get(0)?.imageUrl?.get(0)
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
            setImageGallary(myCurrentVarient.imageUrl)
            var adapterBanner = AdapterProduct(
                requireContext(),
                galleryBundle,
                object : AdapterProduct.OnItemClickListener {
                    override fun onProductClicked(data: String) {
                        goToGalleryView()
                    }

                })
            binding.imageSlider.setSliderAdapter(adapterBanner)
            binding.imageSlider.setInfiniteAdapterEnabled(false)
            binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
            binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        }
    }

    private fun setImageGallary(imageUrlList: List<String>) {
        galleryBundle = imageUrlList
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
            isStockAvailable = false
            binding.imageCart.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.colorGray
                )
            );

            binding.detail2.ivCart.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorGray
                )
            );
            changeColor(tv_stock, R.color.colorRedDark, R.color.white, requireContext())
        } else if (stock.contains("In stock", ignoreCase = true)) {
            binding.imageCart.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.colorBlack
                )
            );

            isStockAvailable = true
            binding.detail2.ivCart.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorBlack
                )
            );
            changeColor(tv_stock, R.color.colorGreenDark, R.color.white, requireContext())

        } else {
            isStockAvailable = true
            binding.imageCart.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.colorBlack
                )
            );

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

}