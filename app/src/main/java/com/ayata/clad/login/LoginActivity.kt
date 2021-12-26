package com.ayata.clad.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import com.ayata.clad.R
import com.ayata.clad.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarLight(R.color.ColorWhite)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
            .replace(R.id.fragments_passwords, FragmentLogin()).commit()
    }

    private fun setStatusBarLight(color:Int){
        val window: Window = this.window
        var flags = window.decorView.systemUiVisibility // get current flag
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
        window.decorView.systemUiVisibility = flags
        window.statusBarColor = resources.getColor(color)
    }
}