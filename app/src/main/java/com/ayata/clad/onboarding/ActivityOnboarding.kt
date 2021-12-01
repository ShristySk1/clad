package com.ayata.clad.onboarding

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.ayata.clad.R
import com.ayata.clad.databinding.ActivityMainBinding.inflate
import com.ayata.clad.databinding.ActivityOnboardingBinding
import com.ayata.clad.databinding.ActivityOnboardingBinding.inflate
import com.ayata.clad.databinding.ItemOnboardingBinding.inflate
import com.ayata.clad.password.LoginandPassword
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ActivityOnboarding : AppCompatActivity(), AdapaterActivityOnboarding.setOnItemClickListener {
    private lateinit var viewPager2: ViewPager2
    lateinit var activityOnboarding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        activityOnboarding = ActivityOnboardingBinding.inflate((layoutInflater))
        setContentView(activityOnboarding.root)
        setStatusBarDark(R.color.ColorBlack)
        viewPager2 = findViewById(R.id.viewpager2)
        val adapter = AdapaterActivityOnboarding(this)
        viewPager2.adapter = adapter

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager2,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab?, position: Int -> }
        ).attach()

        activityOnboarding.btnonboard.setOnClickListener {
            val intent = Intent(applicationContext, LoginandPassword::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onItemClick(position: Int) {

    }

    fun setStatusBarLight(color:Int){
        val window: Window = this.window
        var flags = window.decorView.systemUiVisibility // get current flag
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
        window.decorView.systemUiVisibility = flags
        window.statusBarColor = resources.getColor(color)
    }
    fun setStatusBarDark(color:Int){
        val window: Window = this.window
        var flags = window.decorView.systemUiVisibility // get current flag
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
        flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // use XOR here for remove LIGHT_STATUS_BAR from flags
        window.decorView.systemUiVisibility = flags
        window.statusBarColor = resources.getColor(color)
    }

}