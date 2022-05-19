package com.ayata.clad

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.ActivityMainBinding
import com.ayata.clad.databinding.DialogLoginBinding
import com.ayata.clad.filter.FragmentFilter
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.preorder.FragmentPreorder
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.profile.FragmentProfile
import com.ayata.clad.profile.myorder.order.FragmentOrderDetail
import com.ayata.clad.profile.reviews.FragmentMyReviewsForm
import com.ayata.clad.profile.reviews.model.Review
import com.ayata.clad.search.FragmentSearch
import com.ayata.clad.shop.FragmentShop
import com.ayata.clad.shopping_bag.checkout.FragmentCheckout
import com.ayata.clad.shopping_bag.response.checkout.CartResponse
import com.ayata.clad.shopping_bag.viewmodel.CheckoutViewModel
import com.ayata.clad.shopping_bag.viewmodel.CheckoutViewModelFactory
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.wishlist.FragmentWishlist
import com.ayata.clad.wishlist.response.get.GetWishListResponse
import com.ayata.clad.wishlist.viewmodel.WishListViewModel
import com.ayata.clad.wishlist.viewmodel.WishListViewModelFactory
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var binding: ActivityMainBinding
    private lateinit var badge: BadgeDrawable
    private lateinit var badge_wishlist: BadgeDrawable
    lateinit var viewModelCart: CheckoutViewModel
    lateinit var viewModelWishlist: WishListViewModel

    //for category filter
    var filterSlugCategory = ""

    //for empty view we need to show recommendation
    var listRecommended = ArrayList<ProductDetail>()

    //back button
    var isSureExit = false
    var isFromSameActivity = true
    override fun onCreate(savedInstanceState: Bundle?) {
        setAppMode()
        super.onCreate(savedInstanceState)
        Log.d("oncreatedaata", "onCreate: ");

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
//        handleDynamicLink()
        //set login
        PreferenceHandler.setIsOnBoarding(this, false)
        setUpFirebaseNotification()
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(navListener)
        binding.bottomNavigationView.setOnNavigationItemReselectedListener {
            // Nothing here to disable reselect
        }
        if (findViewById<View?>(R.id.main_fragment) != null) {
            if (savedInstanceState != null) {
                return
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment, FragmentHome())
                .commit()
        }
        setStatusBarLight(R.color.colorWhite)
        setUpViewModel()
        setBadge()
    }

    private fun handleDynamicLink() {
        val data = this.intent.data
//        Log.d("testparams0", "handleDynamicLink: "+intent.extras);
//        val parametros = intent.extras
//
//        if (parametros != null) {
//            val productId: Int = parametros.getInt("product_id")
//            if (productId != null) {
//                //do whatever you have to
//                //...
//                Log.d("testproductId", "handleDynamicLink: "+productId);
//            }
//        } else {
//            //no extras, get over it!!
//            Log.d("testproductId", "handleDynamicLink: null");
//
//        }
//        if (data != null && data.isHierarchical) {
//            Log.d("testparams", "handleDynamicLink: "+data);
//            if (data.getQueryParameter("product_id") != null) {
//                val param = data.getQueryParameter("product_id")
//                Log.d("theparamis", param!!)
//                //do something here
//                val bundle = Bundle()
//                bundle.putInt(
//                    FragmentHome.PRODUCT_DETAIL_ID,
//                    param.toInt()
//                )
//                val fragmentProductDetail = FragmentProductDetail()
//                fragmentProductDetail.arguments = bundle
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.main_fragment, fragmentProductDetail)
//                    .addToBackStack(null)
//                    .commit()
//            }
//        }
//        Firebase.dynamicLinks
//            .getDynamicLink(intent)
//            .addOnSuccessListener(this) { pendingDynamicLinkData ->
//                // Get deep link from result (may be null if no link is found)
//                var deepLink: Uri? = null
//                if (pendingDynamicLinkData != null) {
//                    deepLink = pendingDynamicLinkData.link
//                    Log.d("TAG", "==> ${deepLink.toString()}")
//                    if (deepLink?.getBooleanQueryParameter("product_id", false) == true) {
////                         deepLink.getQueryParameter("product_id")
//                        //open fragment product
//                        val bundle = Bundle()
//                        deepLink.getQueryParameter("product_id")?.let {
//                            bundle.putInt(FragmentHome.PRODUCT_DETAIL_ID,
//                                it?.toInt()
//                            )
//                        }
//                        val fragmentProductDetail = FragmentProductDetail()
//                        fragmentProductDetail.arguments = bundle
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.main_fragment, fragmentProductDetail)
//                            .addToBackStack(null)
//                            .commit()
//                    }
//                }
//
//                // Handle the deep link. For example, open the linked
//                // content, or apply promotional credit to the user's
//                // account.
//                // ...
//
//                // ...
//            }
//            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        val parametros = intent?.extras
//        if (parametros != null) {
//            val productId: Int = parametros.getInt("product_id")
//            if (productId != null) {
//                //do whatever you have to
//                //...
//                Log.d("testproductId", "handleDynamicLink: " + productId);
//            }
//        } else {
//            //no extras, get over it!!
//            Log.d("testproductId", "handleDynamicLink: null");
//
//        }
//
//
//    }

    private fun setAppMode() {
        val isDarkMode = PreferenceHandler.isThemeDark(this)!!
        Log.d(TAG, "setAppMode: " + isDarkMode);
        val currentMode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        Log.d("testmode", "setAppMode: " + AppCompatDelegate.getDefaultNightMode());
        if (currentMode != AppCompatDelegate.getDefaultNightMode()) {
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate
                        .MODE_NIGHT_YES
                )
            } else {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate
                        .MODE_NIGHT_NO
                )
            }
        }
    }

    private fun setUpFirebaseNotification() {
        //for orea>=
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Clad",
                "i m name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("myfirebasetoken", "onComplete: " + task.result)
            }
        }
    }

    private fun setUpViewModel() {
//        viewModelCart = ViewModelProvider(this).get(CheckoutViewModel::class.java)
        viewModelCart = ViewModelProvider(
            this,
            CheckoutViewModelFactory(ApiRepository(ApiService.getInstance(this)))
        )[CheckoutViewModel::class.java]
        viewModelWishlist = ViewModelProvider(
            this,
            WishListViewModelFactory(ApiRepository(ApiService.getInstance(this)))
        ).get(WishListViewModel::class.java)
    }

    private fun setBadge() {
        badge = binding.bottomNavigationView.getOrCreateBadge(R.id.nav_cart)
        badge_wishlist = binding.bottomNavigationView.getOrCreateBadge(R.id.nav_favorite)
        badge.badgeTextColor = ContextCompat.getColor(this@MainActivity, R.color.white)
        badge_wishlist.badgeTextColor = ContextCompat.getColor(this@MainActivity, R.color.white)
        badge.isVisible = false
        badge_wishlist.isVisible = false
        NavCount.addMyBooleanListener(object :
            NavCountChangeListener {
            override fun onCartCountChange(count: Int?) {
                if (count != null) {
                    if (count != 0) {
                        badge.isVisible = true
                        badge.number = count
                    } else {
                        badge.isVisible = false
                        badge.clearNumber()
                    }
                }
            }

            override fun onWishListCountChange(count: Int?) {
                if (count != null) {
                    if (count != 0) {
                        badge_wishlist.isVisible = true
                        badge_wishlist.number = count
                    } else {
                        badge_wishlist.isVisible = false
                        badge_wishlist.clearNumber()
                    }
                }
            }
        })
        //call cart
        getCartAPI()
        getWishListAPI()
    }

    private fun getCartAPI() {
        viewModelCart.cartListAPI(PreferenceHandler.getToken(this).toString())
        viewModelCart.getCartListAPI().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val message = jsonObject.get("message").asString
                            if (message.contains("empty.", true)) {
                                //empty
                                NavCount.myBoolean = 0
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "getCartAPI:Error ${e.message}")
                            try {
                                val checkoutResponse =
                                    Gson().fromJson<CartResponse>(
                                        jsonObject,
                                        CartResponse::class.java
                                    )
                                if (checkoutResponse.cart != null) {
                                    if (checkoutResponse.cart.size > 0) {
                                        val cartlist = checkoutResponse.cart
                                        //set cart number
                                        NavCount.myBoolean = cartlist.size
                                    } else {
                                        //empty
                                        NavCount.myBoolean = 0
                                    }

                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    Log.d(TAG, "getCartAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun getWishListAPI() {
        viewModelWishlist.wishListAPI(PreferenceHandler.getToken(this).toString())
        viewModelWishlist.getWishListAPI().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val message = jsonObject.get("message").asString
                            if (message.contains("Your wishlist is empty.", true)) {
                                //empty
                                NavCount.myWishlist = 0
                            }

                        } catch (e: Exception) {
                            try {
                                val wishListResponse =
                                    Gson().fromJson<GetWishListResponse>(
                                        jsonObject,
                                        GetWishListResponse::class.java
                                    )
                                if (wishListResponse.wishlist != null) {
                                    if (wishListResponse.wishlist.size > 0) {
                                        val wishlist = wishListResponse.wishlist
                                        //set count
                                        NavCount.myWishlist = wishlist.size
                                    } else {
                                        //empty
                                        NavCount.myWishlist = 0
                                    }

                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Log.d(TAG, "getWishListAPI:Error ${it.message}")
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val extras = intent.extras
        if (extras != null) {
            Log.d("testintent", "onStart: " + extras.getBoolean(Constants.FROM_STORY));
            val value = extras.getBoolean(Constants.FROM_STORY, false)
            val data = extras.getSerializable("data") as ProductDetail?
            if (value && data != null) {
                fromStory(data)
            } else {
                isFromSameActivity = true
            }
        } else {
            isFromSameActivity = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.d("oncreatedaata", "onCreate: ");
    }

    private fun fromStory(data: ProductDetail) {
        isFromSameActivity = false
        Log.d("testintent", "fromStory: " + data);
        val bundle = Bundle()
        bundle.putSerializable(FragmentHome.PRODUCT_DETAIL, data)
        val fragmentProductDetail = FragmentProductDetail()
        fragmentProductDetail.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentProductDetail)
            .commit()
        intent.replaceExtras(Bundle())
        intent.action = ""
        intent.data = null
        intent.flags = 0
    }

    fun showBottomNavigation(show: Boolean) {
        if (show) {
//            binding.bottomNavigationView.visibility = View.VISIBLE
            binding.bottomNavigationView.slideVisibility(show)
        } else {
            binding.bottomNavigationView.visibility = View.GONE
        }

    }

    private fun exitFullScreen() {
        window.apply {
            apply {
                clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                decorView.systemUiVisibility = 0
            }
        }
        setStatusBarLight(R.color.colorWhite)
    }


    private var itemClearAllClick: (() -> Unit)? = null
    fun setClearAllListener(listener: (() -> Unit)) {
        itemClearAllClick = listener
    }

    private fun setToolbar() {
        binding.appbar.btnSearch.setOnClickListener {
//            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, FragmentSearch())
                .addToBackStack(null)
                .commit()
        }

        binding.appbar.btnClear.setOnClickListener {
            Toast.makeText(this, "Clear All", Toast.LENGTH_SHORT).show()
            itemClearAllClick?.let {
                it()
            }
        }

        binding.appbar.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.appbar.btnClose.setOnClickListener {
//            FragmentFilter.MY_OLD_LIST.forEach {
//                Log.d("testoldlist", "onCreateView: " + it);
//            }

            onBackPressed()

//            FragmentFilter.MY_LIST = FragmentFilter.MY_OLD_LIST.toCollection(mutableListOf()).toList()
//            hideAndShowFragment(true)
        }

        binding.appbar.btnCloseProfile.setOnClickListener {
            onBackPressed()
        }

        binding.appbar.btnFilter.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.main_fragment, FragmentFilter())
                .addToBackStack(null)
                .commit()
        }
        binding.appbar.btnFilter2.setOnClickListener {
//            hideAndShowFragment(false)
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.main_fragment, FragmentFilter())
                .addToBackStack(null)
                .commit()
        }

        binding.appbar.btnProfile.setOnClickListener {
//            if (PreferenceHandler.getToken(this) != "") {
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.main_fragment, FragmentProfile())
//                    .addToBackStack(null)
//                    .commit()
//            } else {
//                showDialogLogin()
//            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, FragmentProfile())
                .addToBackStack(null)
                .commit()
        }
    }

    fun hideAndShowFragment(hide: Boolean) {
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        val pf: FragmentFilter? =
            fm.findFragmentByTag("filter") as FragmentFilter?
        ft.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        if (hide) {
            Log.d("testshow", "hideAndShowFragment: " + hide);
            pf?.let {
                ft.hide(pf).commit()
            }
        } else {
            Log.d("testshow", "hideAndShowFragment: " + hide);
            pf?.let {
                ft.show(pf).commit()
            } ?: kotlin.run {
                ft.add(R.id.main_fragment, FragmentFilter(), "filter").commit();

            }

        }
    }

    fun setFilterSlugFromCategory(slug: String) {
        filterSlugCategory = slug
    }

    fun getFilterSlug() = filterSlugCategory
    fun setToolbar1(
        title: String,
        isSearch: Boolean,
        isProfile: Boolean,
        isClose: Boolean,
        isLogo: Boolean = false,
        isFilter: Boolean? = false,
    ) {
        exitFullScreen()
        binding.appbar.appbar1.visibility = View.VISIBLE
        binding.appbar.appbar2.visibility = View.GONE

        binding.appbar.textTitle.text = title

        if (isSearch) {
            binding.appbar.btnSearch.visibility = View.VISIBLE
        } else {
            binding.appbar.btnSearch.visibility = View.GONE
        }

        if (isProfile) {
            binding.appbar.btnProfile.visibility = View.VISIBLE
        } else {
            binding.appbar.btnProfile.visibility = View.GONE
        }

        if (isClose) {
            binding.appbar.btnCloseProfile.visibility = View.VISIBLE
        } else {
            binding.appbar.btnCloseProfile.visibility = View.GONE
        }

        if (isLogo) {
            binding.appbar.layoutLogo.visibility = View.VISIBLE
        } else {
            binding.appbar.layoutLogo.visibility = View.GONE
        }
        isFilter?.let {
            if (isFilter) {
                Log.d("filteractive", "setToolbar1: ");
                binding.appbar.btnFilter.visibility = View.VISIBLE
            } else {
                binding.appbar.btnFilter.visibility = View.GONE
            }
        }


    }

    fun setToolbar2(
        isClose: Boolean,
        isBack: Boolean,
        isFilter: Boolean,
        isClear: Boolean,
        textTitle: String,
        textDescription: String
    ) {
        exitFullScreen()
        binding.appbar.appbar1.visibility = View.GONE
        binding.appbar.appbar2.visibility = View.VISIBLE

        if (isFilter) {
//            binding.appbar.layout2.visibility=View.VISIBLE
            binding.appbar.btnFilter2.visibility = View.VISIBLE
        } else {
            binding.appbar.btnFilter2.visibility = View.GONE
        }

        if (isClose) {
            binding.appbar.btnClose.visibility = View.VISIBLE
        } else {
            binding.appbar.btnClose.visibility = View.GONE
        }

        if (isBack) {
            binding.appbar.btnBack.visibility = View.VISIBLE
        } else {
            binding.appbar.btnBack.visibility = View.GONE
        }

        if (isClear) {
            binding.appbar.btnClear.visibility = View.VISIBLE
        } else {
            binding.appbar.btnClear.visibility = View.GONE
        }

        if (textDescription.isEmpty() || textDescription.isBlank()) {
            binding.appbar.description.visibility = View.GONE
        } else {
            binding.appbar.description.visibility = View.VISIBLE
        }

        binding.appbar.title.text = textTitle
        binding.appbar.description.text = textDescription

    }

    fun showToolbar(show: Boolean) {
        exitFullScreen()
        if (show) {
            binding.appbar.root.visibility = View.VISIBLE
        } else {
            binding.appbar.root.visibility = View.GONE
        }
    }

    fun showToolbarVisibility(show: Boolean) {
        exitFullScreen()
        if (show) {
            binding.appbar.root.slideVisibilityToolbar(true)

        } else {
            binding.appbar.root.visibility = View.GONE
        }
    }

    private fun setStatusBarLight(color: Int) {
        val window: Window = this.window
        var flags = window.decorView.systemUiVisibility // get current flag
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
        if (PreferenceHandler.isThemeDark(this)!!) {
            flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.decorView.systemUiVisibility = flags
        window.statusBarColor = ContextCompat.getColor(this, color)
    }

    private fun View.slideVisibility(visibility: Boolean, durationTime: Long = 250) {
        val transition = Slide(Gravity.BOTTOM)
        transition.apply {
            duration = durationTime
            addTarget(this@slideVisibility)
        }
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.isVisible = visibility
    }

    private fun View.slideVisibilityToolbar(visibility: Boolean, durationTime: Long = 500) {
//        val transition = Fade()
//        transition.apply {
//            duration = durationTime
//            addTarget(this@slideVisibilityToolbar)
//        }
//        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.isVisible = visibility

    }

    private val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem: MenuItem ->
            var selectedFragment: Fragment? = null
            var changeFragment = true
            when (menuItem.itemId) {
                R.id.nav_home -> selectedFragment = FragmentHome()
                R.id.nav_hanger -> selectedFragment = FragmentShop()
                R.id.nav_favorite -> {
                    if (PreferenceHandler.getToken(this) != "") {
                        selectedFragment = FragmentWishlist()
                    } else {
                        changeFragment = false
                        showDialogLogin()
                    }
                }
                R.id.nav_cart -> {
                    if (PreferenceHandler.getToken(this) != "") {
                        selectedFragment = FragmentCheckout()
                    } else {
                        changeFragment = false
                        showDialogLogin()
                    }
                }
                R.id.nav_rader -> selectedFragment = FragmentPreorder()
            }
            isSureExit = false
            if (changeFragment) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, selectedFragment!!)
                    .commit()
            }
            true
        }

    override fun onBackPressed() {


        val fm = supportFragmentManager
        for (frag in fm.fragments) {
            if (frag.isVisible) {
                val childFm = frag.childFragmentManager
                if (childFm.backStackEntryCount > 0) {
                    childFm.popBackStackImmediate()
                    Log.d("BackCheck", "onBackPressed: childFrag")
                    return
                }
            }
        }
        Log.d("BackCheck", "onBackPressed: normal" + supportFragmentManager.backStackEntryCount)

//        if (binding.bottomNavigationView.getSelectedItemId()!= R.id.nav_home) {
//            binding.bottomNavigationView.setSelectedItemId(R.id.nav_home);
//
//        } else {
//            super.onBackPressed();
//        }
        if (!isSureExit && isFromSameActivity) {
            if (supportFragmentManager.backStackEntryCount == 0) {
                Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show()
                isSureExit = true
                return
            }
        }
        super.onBackPressed()

    }

    fun openHomePage() {
        startActivity(intent) // start same activity
        finish() // destroy older activity
        overridePendingTransition(0, 0) // this is important for seamless transition
    }

    fun openFragmentShop() {
        binding.bottomNavigationView.selectedItemId = R.id.nav_hanger
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, FragmentShop())
            .addToBackStack(null)
            .commit()
    }

    fun removeAllFragment() {
        binding.mainFragment.removeAllViews()
    }

    override fun recreate() {
        super.recreate()
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    interface NavCountChangeListener {
        fun onCartCountChange(count: Int?)
        fun onWishListCountChange(count: Int?)

    }


    object NavCount {
        private var myCartCount: Int? = null
        private var myWishListCount: Int? = null

        private val listeners: MutableList<NavCountChangeListener> = ArrayList()
        var myBoolean: Int?
            get() = myCartCount
            set(value) {
                myCartCount = value
                println("testtttttttttttttttt" + myCartCount)
                for (l in listeners) {
                    l.onCartCountChange(myCartCount)
                }
            }
        var myWishlist: Int?
            get() = myWishListCount
            set(value) {
                myWishListCount = value
                println("testtttttttttttttttt" + myWishListCount)
                for (l in listeners) {
                    l.onWishListCountChange(myWishListCount)
                }
            }

        fun addMyBooleanListener(l: NavCountChangeListener) {
            listeners.add(l)
        }
    }

    fun showDialogLogin() {
        val dialogBinding = DialogLoginBinding.inflate(LayoutInflater.from(this))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogBinding.root)
        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        dialogBinding.btnSave.setOnClickListener {
            //google login
            signIn()
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    fun saveRecommendationInMainActivty(listGiven: List<ProductDetail>?) {
        listRecommended.clear()
        if (listGiven != null) {
            listRecommended.addAll(listGiven)
        }
    }

    fun getRecommendedList(): List<ProductDetail> {
        return listRecommended
    }

    fun openOrderDetail(frag: FragmentOrderDetail) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            .replace(R.id.main_fragment, frag)
            .addToBackStack("order_list")
            .commit()
    }

    fun openOrderList(frag: FragmentProfile) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, frag)
            .commit()
    }

    fun openFragmentReviewForm(it: Review) {
        val bundle = Bundle()
        val frag = FragmentMyReviewsForm()
        bundle.putSerializable("datas", it)
        frag.arguments = bundle
        supportFragmentManager.beginTransaction().replace(
            R.id.main_fragment,
            frag
        ).addToBackStack(null).commit()

    }

    fun showSnakbarBottomOffset(message: String) {
        val snackbar = Snackbar
            .make(binding.root, message.removeDoubleQuote(), Snackbar.LENGTH_SHORT)
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.anchorView = binding.bottomNavigationView
        snackbar.show()
    }

    fun showSnakbar(message: String, flag: String? = null) {
        Log.d(TAG, "showSnakbar: " + message);
        val snackbar = Snackbar
            .make(binding.root, message.removeDoubleQuote(), 1000)
        snackbar.setActionTextColor(Color.RED)
        flag?.let {
            when (it) {
                Constants.GO_TO_CART -> {
                    snackbar.setAction("CART", View.OnClickListener {

                        binding.bottomNavigationView.selectedItemId = R.id.nav_cart

                    })
                }
                Constants.GO_TO_WISHLIST -> {
                    snackbar.setAction("WISHLIST", View.OnClickListener {
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.main_fragment, FragmentWishlist())
//                            .commit()
                        binding.bottomNavigationView.selectedItemId = R.id.nav_favorite

                    })
                }
                else -> {
                }
            }
        }
        snackbar.show()
    }

    fun isFilter(b: Boolean) {
        if(b)binding.appbar.btnFilter2.visibility = View.VISIBLE else binding.appbar.btnFilter2.visibility = View.GONE
    }
}