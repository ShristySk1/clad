package com.ayata.clad

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import com.ayata.clad.data.preference.DataStoreManager
import com.ayata.clad.databinding.ActivitySplashBinding
import com.ayata.clad.onboarding.ActivityOnboarding
import com.ayata.clad.utils.PreferenceHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySplashBinding

    private var goTo:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listDrawable.shuffle()
        setImageLoop()
        setUpFullScreen()
        Handler().postDelayed({
            nextActivity()
        }, 2000)
    }

    private fun setUpFullScreen() {
        this.let {
            it.window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                statusBarColor = Color.TRANSPARENT
            }
        }
    }

    private var listDrawable= arrayListOf<Int>(R.drawable.brand_aamayra,
        R.drawable.brand_aroan,R.drawable.brand_bishrom,
        R.drawable.brand_caliber,R.drawable.brand_creative_touch,
        R.drawable.brand_fibro,R.drawable.brand_fuloo,
        R.drawable.brand_gofi,R.drawable.brand_goldstar,
        R.drawable.brand_hillsandclouds,R.drawable.brand_jujuwears,
        R.drawable.brand_kasa,R.drawable.brand_ktm_city,R.drawable.brand_logo,
        R.drawable.brand_mode23,R.drawable.brand_newmew,
        R.drawable.brand_phalanoluga,R.drawable.brand_sabah,
        R.drawable.brand_station,R.drawable.brand_tsarmoire)

    private fun setImageLoop(){
        val animationFadeIn: Animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        val animationFadeOut: Animation = AnimationUtils.loadAnimation(this,  android.R.anim.fade_out)
        animationFadeIn.duration=225
        animationFadeOut.duration=226
        var count=0
        val animListener: Animation.AnimationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (animation == animationFadeIn) {
                    // Start fade-out animation
                    binding.imageLoop.startAnimation(animationFadeOut)

                } else if (animation == animationFadeOut) {
                    if (count < listDrawable.size-1) {
                        count++
                    } else {
                        count = 0
                    }
                    binding.imageLoop.setImageResource(listDrawable[count])
                    binding.imageLoop.startAnimation(animationFadeIn)
                }
            }
        }
        animationFadeOut.setAnimationListener(animListener)
        animationFadeIn.setAnimationListener(animListener)
        binding.imageLoop.setImageResource(listDrawable[0])
        binding.imageLoop.startAnimation(animationFadeIn)
    }

    override fun onStart() {
        super.onStart()
        if(PreferenceHandler.isThemeDark(this)){
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate
                    .MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate
                    .MODE_NIGHT_NO)
        }
        GlobalScope.launch(Dispatchers.IO) {
            DataStoreManager(this@SplashActivity).getToken().catch { e ->
                e.printStackTrace()
            }.collect {
                withContext(Dispatchers.Main) {
                    val token=it
                    goTo = if(!token.isNullOrBlank()&& !token.isNullOrEmpty()){
                        2
                    }else{
                        1
                    }
                }
            }
        }
    }

    private fun nextActivity(){
//        if(goTo==1){
//            startActivity(Intent(this@SplashActivity, ActivityOnboarding::class.java))
//            finish()
//        }else if(goTo==2){
//            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//            finish()
//        }
        startActivity(Intent(this@SplashActivity, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        finish()
    }
}