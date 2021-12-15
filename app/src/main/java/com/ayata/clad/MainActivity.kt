package com.ayata.clad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.ayata.clad.databinding.ActivityMainBinding
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.shop.FragmentShop
import com.ayata.clad.shopping_bag.FragmentShoppingBag
import com.ayata.clad.thrift.FragmentThrift
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
                .add(R.id.main_fragment, FragmentHome())
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

    fun showToolbar1(istrue: Boolean)
    {
        if(istrue)
        {
            activityMainBinding.appbar.appbar1.visibility=View.VISIBLE
        }else{
            activityMainBinding.appbar.appbar1.visibility=View.GONE
        }
    }

    private val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem: MenuItem ->
            var selectedFragment: Fragment? = null
            when (menuItem.itemId) {
                R.id.nav_home -> selectedFragment =FragmentHome()
                R.id.nav_hanger -> selectedFragment = FragmentShop()
                R.id.nav_favorite -> selectedFragment = FragmentShoppingBag()
                R.id.nav_addcart -> selectedFragment = FragmentHome()
                R.id.nav_rader -> selectedFragment=FragmentThrift()
            }
            supportFragmentManager.beginTransaction()
//                .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .replace(R.id.main_fragment, selectedFragment!!)
                .commit()
            true
        }


}