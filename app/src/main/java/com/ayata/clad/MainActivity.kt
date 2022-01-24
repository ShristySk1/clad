package com.ayata.clad

import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ayata.clad.databinding.ActivityMainBinding
import com.ayata.clad.filter.FragmentFilter
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.preorder.FragmentPreorder
import com.ayata.clad.profile.FragmentProfile
import com.ayata.clad.shop.FragmentShop
import com.ayata.clad.shopping_bag.FragmentShoppingBag
import com.ayata.clad.wishlist.FragmentWishlist
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(navListener)
        if (findViewById<View?>(R.id.main_fragment) != null) {
            if (savedInstanceState != null) {
                return
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment, FragmentHome())
                .commit()
        }
        setStatusBarLight(R.color.white)
        setToolbar()

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
        setStatusBarLight(R.color.white)
    }

    private fun setToolbar() {
        binding.appbar.btnSearch.setOnClickListener {
//            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.main_fragment, TestFragment())
//                .addToBackStack(null)
//                .commit()
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
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, FragmentProfile())
                .addToBackStack(null)
                .commit()
        }
    }

    fun setToolbar1(title: String, isSearch: Boolean, isProfile: Boolean, isClose: Boolean, isLogo:Boolean=false) {
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

        binding.appbar.title.text = textTitle
        binding.appbar.description.text = textDescription

    }

    fun showToolbar(show: Boolean) {
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
//        flags=flags xor  View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
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
            when (menuItem.itemId) {
                R.id.nav_home -> selectedFragment = FragmentHome()
                R.id.nav_hanger -> selectedFragment = FragmentShop()
                R.id.nav_favorite -> selectedFragment = FragmentWishlist()
                R.id.nav_cart -> selectedFragment = FragmentShoppingBag()
                R.id.nav_rader -> selectedFragment = FragmentPreorder()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, selectedFragment!!)
                .commit()
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

    fun openFragmentShop(){
        binding.bottomNavigationView.selectedItemId=R.id.nav_hanger
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment,FragmentShop())
            .addToBackStack(null)
            .commit()
    }

    fun removeAllFragment(){
        binding.mainFragment.removeAllViews()
    }

}