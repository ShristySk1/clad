package com.ayata.clad.onboarding

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.ayata.clad.R
import com.ayata.clad.databinding.ActivityOnboardingBinding
import com.ayata.clad.password.LoginandPassword
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ActivityOnboarding : AppCompatActivity(), AdapaterActivityOnboarding.setOnItemClickListener {
    private lateinit var viewPager2: ViewPager2
    lateinit var activityOnboarding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
        setUpFullScreen()
        activityOnboarding = ActivityOnboardingBinding.inflate((layoutInflater))
        setContentView(activityOnboarding.root)
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

    private fun setUpFullScreen() {
        this?.let {
            it.window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                statusBarColor = Color.TRANSPARENT
            }
        }
    }

    override fun onItemClick(position: Int) {

    }


}