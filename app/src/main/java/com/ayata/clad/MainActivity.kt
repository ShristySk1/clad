package com.ayata.clad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ayata.clad.databinding.ActivityMainBinding
import com.ayata.clad.filter.FragmentFilter
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.shop.FragmentShop
import com.ayata.clad.shopping_bag.FragmentShoppingBag
import com.ayata.clad.thrift.FragmentThrift
import com.ayata.clad.order.FragmentOrderDetail
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.product.productlist.FragmentProductList
import com.ayata.clad.profile.FragmentProfile
import com.ayata.clad.wishlist.FragmentWishlist
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        showToolbar1(false)
        showToolbar2(true)
        InitshowFirsttoolbar()
        activityMainBinding.bottomNavigationView.setOnNavigationItemSelectedListener(navListener)
        if (findViewById<View?>(R.id.main_fragment) != null) {
            if (savedInstanceState != null) {
                return
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment, FragmentOrderDetail())
                .commit()
        }
    }


    fun showToolbar2(istrue:Boolean) {
        if(istrue) {
            activityMainBinding.appbar.appbar2.visibility = View.VISIBLE
        }else{
            activityMainBinding.appbar.appbar2.visibility=View.GONE
        }
    }

    fun  InitshowFirsttoolbar()
    {
        activityMainBinding.appbar.endTextAppbarEnd.text="DROPS"
    }
fun hideBottomNavigation(){

}
    fun showToolbar1(istrue: Boolean)
    {
        if(istrue)
        {
            activityMainBinding.appbar.appbar1.visibility=View.VISIBLE
        }else{
            activityMainBinding.appbar.appbar1.visibility=View.GONE
        }
    }
    fun showBottomNav(show: Boolean) {
        activityMainBinding.bottomNavigationView.slideVisibility(show)
    }
    fun hideToolbar() {
        activityMainBinding.appbar.root.visibility = View.GONE
    }
    fun setStatusBarLight(color: Int) {
        val window: Window = this.window
        var flags = window.decorView.systemUiVisibility // get current flag
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
        window.decorView.systemUiVisibility = flags
        window.statusBarColor = ContextCompat.getColor(this, color)
    }
    private fun View.slideVisibility(visibility: Boolean, durationTime: Long = 300) {
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
                R.id.nav_home -> selectedFragment =FragmentProductDetail()
                R.id.nav_hanger -> selectedFragment = FragmentProductList()
                R.id.nav_favorite -> selectedFragment = FragmentFilter()
                R.id.nav_addcart -> selectedFragment = FragmentProfile()
                R.id.nav_rader -> selectedFragment=FragmentWishlist()
            }
            supportFragmentManager.beginTransaction()
//                .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .replace(R.id.main_fragment, selectedFragment!!)
                .commit()
            true
        }


}