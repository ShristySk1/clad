package com.ayata.clad.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.viewpager2.widget.ViewPager2
import com.ayata.clad.R
import com.ayata.clad.databinding.ActivityMainBinding.inflate
import com.ayata.clad.databinding.ActivityOnboardingBinding
import com.ayata.clad.databinding.ActivityOnboardingBinding.inflate
import com.ayata.clad.databinding.ItemOnboardingBinding.inflate
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ActivityOnboarding : AppCompatActivity(),AdapaterActivityOnboarding.setOnItemClickListener {
    private lateinit var viewPager2: ViewPager2
    lateinit var activityOnboarding: ActivityOnboardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        activityOnboarding=ActivityOnboardingBinding.inflate((layoutInflater))
        setContentView(activityOnboarding.root)
        viewPager2=findViewById(R.id.viewpager2)
        val adapter=AdapaterActivityOnboarding(this)
        viewPager2.adapter=adapter

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager2,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab?, position: Int -> }
        ).attach()
    }

    override fun onItemClick(position: Int) {

    }
}