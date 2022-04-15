package com.ayata.clad.brand

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ayata.clad.R
import com.ayata.clad.databinding.ActivityBrandDetailBinding
import com.ayata.clad.databinding.ActivityMainBinding
import com.ayata.clad.home.FragmentHome

class BrandDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBrandDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrandDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (findViewById<View?>(R.id.brand_fragment) != null) {
            if (savedInstanceState != null) {
                return
            }
            val frag=FragmentBrandDetail.newInstance(intent.getStringExtra("slug")?:"")
            supportFragmentManager.beginTransaction()
                .add(R.id.brand_fragment, frag)
                .commit()
        }
    }
}