package com.ayata.clad

import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var binding: ActivityMainBinding
    private lateinit var badge: BadgeDrawable
    private lateinit var badge_wishlist: BadgeDrawable
    lateinit var viewModelCart: CheckoutViewModel
    lateinit var viewModelWishlist: WishListViewModel

    //for empty view we need to show recommendation
    var listRecommended = ArrayList<ProductDetail>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //set login
        PreferenceHandler.setIsOnBoarding(this, false)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(navListener)
        if (findViewById<View?>(R.id.main_fragment) != null) {
            if (savedInstanceState != null) {
                return
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment, FragmentHome())
                .commit()
        }
        setStatusBarLight(R.color.colorWhite)
        setToolbar()
        setUpViewModel()

        setBadge()

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
        badge.isVisible = false
        badge_wishlist.isVisible = false
        NavCount.addMyBooleanListener(object :
            NavCountChangeListener {
            override fun onCartCountChange(count: Int?) {
                if (count != null) {
                    if (count != 0) {
                        badge.isVisible = true
                        badge.number = count
                        Log.d(TAG, "setBadge: badgedone")
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
                        Log.d(TAG, "setBadge: badgedone")
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
            val value = extras.getBoolean(Constants.FROM_STORY, false)
            if (value) {
                fromStory()
            }
        }
    }

    private fun fromStory() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, FragmentProductDetail())
            .commit()
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
        }

        binding.appbar.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.appbar.btnClose.setOnClickListener {
            onBackPressed()
        }

        binding.appbar.btnCloseProfile.setOnClickListener {
            onBackPressed()
        }

        binding.appbar.btnFilter.setOnClickListener {
            supportFragmentManager.beginTransaction()
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

    fun setToolbar1(
        title: String,
        isSearch: Boolean,
        isProfile: Boolean,
        isClose: Boolean,
        isLogo: Boolean = false
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
            binding.appbar.btnFilter.visibility = View.VISIBLE
        } else {
            binding.appbar.btnFilter.visibility = View.GONE
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
        Log.d("BackCheck", "onBackPressed: normal")
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
}