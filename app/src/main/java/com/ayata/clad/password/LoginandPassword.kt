package com.ayata.clad.password

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.ayata.clad.R
import com.ayata.clad.databinding.ActivityLoginandPasswordBinding
import com.ayata.clad.onboarding.ActivityOnboarding

class LoginandPassword : AppCompatActivity() {
    lateinit var activityLoginandPasswordBinding: ActivityLoginandPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginandPasswordBinding= ActivityLoginandPasswordBinding.inflate(layoutInflater)
        setContentView(activityLoginandPasswordBinding.root)
        setStatusBarLight(R.color.ColorWhite)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
            .replace(R.id.fragments_passwords, Fragment_login()).commit()
    }




    fun setStatusBarLight(color:Int){
        val window: Window = this.window
        var flags = window.decorView.systemUiVisibility // get current flag
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
        window.decorView.systemUiVisibility = flags
        window.statusBarColor = resources.getColor(color)
    }
}