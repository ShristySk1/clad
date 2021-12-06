package com.ayata.clad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ayata.clad.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        showToolbar1(false)
        showToolbar2(true)
        InitshowFirsttoolbar()
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


}