package com.ayata.clad.onboarding

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.viewpager2.widget.ViewPager2
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.preference.DataStoreManager
import com.ayata.clad.databinding.ActivityOnboardingBinding
import com.ayata.clad.login.LoginActivity
import com.ayata.clad.login.TAG
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
            val intent = Intent(applicationContext, LoginActivity::class.java)
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

    override fun onStart() {
        super.onStart()
        GlobalScope.launch(Dispatchers.IO) {
            DataStoreManager(this@ActivityOnboarding).getToken().catch { e ->
                e.printStackTrace()
            }.collect {
                withContext(Dispatchers.Main) {
                    val token=it
                    if(!token.isNullOrBlank()&& !token.isNullOrEmpty()){
                        startActivity(Intent(this@ActivityOnboarding, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

}