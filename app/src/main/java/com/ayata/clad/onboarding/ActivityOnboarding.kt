package com.ayata.clad.onboarding

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.ayata.clad.BaseActivity
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.ActivityOnboardingBinding
import com.ayata.clad.login.TAG
import com.ayata.clad.login.viewmodel.LoginViewModel
import com.ayata.clad.login.viewmodel.LoginViewModelFactory
import com.ayata.clad.utils.PreferenceHandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class ActivityOnboarding :  BaseActivity<ActivityOnboardingBinding>(ActivityOnboardingBinding::inflate), AdapaterActivityOnboarding.setOnItemClickListener {
    private lateinit var viewPager2: ViewPager2
    lateinit var activityOnboarding: ActivityOnboardingBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
        setUpFullScreen()
//        setupViewModel()
        activityOnboarding = ActivityOnboardingBinding.inflate((layoutInflater))
        setContentView(activityOnboarding.root)
//        setUpGoogle()
        viewPager2 = findViewById(R.id.viewpager2)
        val adapter = AdapaterActivityOnboarding(this)
        viewPager2.adapter = adapter
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager2,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab?, position: Int -> }
        ).attach()

        activityOnboarding.btnonboard.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        activityOnboarding.btnongoogle.setOnClickListener {
            signIn()
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
        if(!(PreferenceHandler.getShowOnBoarding(this)?:true)){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

}