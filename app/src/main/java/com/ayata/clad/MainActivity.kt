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
        initToolbar()

    }

    private fun initToolbar() {
        appbar.back.setOnClickListener {
            onBackPressed()
        }

        appbar.dashboard.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(
                R.id.main_fragment,
                FragmentWatchList()
            ).addToBackStack(null).commit()

        }
    }

    fun showToolbar1() {
        appbar.visibility = View.VISIBLE
        appbar.back.visibility = View.GONE
        appbar.layout_text.visibility = View.GONE
        appbar.appbar1.visibility = View.VISIBLE
    }

    fun showToolbar2() {
        appbar.visibility = View.VISIBLE
        appbar.appbar1.visibility = View.GONE
        appbar.layout_text.visibility = View.GONE
        appbar.back.visibility = View.VISIBLE
    }

    fun showToolbar3(name: String, abbreviation: String) {
        appbar.visibility = View.VISIBLE
        appbar.back.visibility = View.VISIBLE
        appbar.layout_text.visibility = View.VISIBLE
        appbar.appbar1.visibility = View.GONE
        appbar.title.text = name
        appbar.abbreviation.text = abbreviation
    }
}