package com.ayata.clad.profile.reviews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.ActivityMainBinding
import com.ayata.clad.databinding.ActivityReviewFromBinding

class ReviewFromActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewFromBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewFromBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAppbar()
    }

    private fun initAppbar() {
        setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Review",
            textDescription = ""
        )
    }
    fun setToolbar2(
        isClose: Boolean,
        isBack: Boolean,
        isFilter: Boolean,
        isClear: Boolean,
        textTitle: String,
        textDescription: String
    ) {
        binding.appbar.appbar1.visibility = View.GONE
        binding.appbar.appbar2.visibility = View.VISIBLE

        if (isFilter) {
            binding.appbar.btnFilter.visibility = View.VISIBLE
        } else {
            binding.appbar.btnFilter.visibility = View.GONE
        }

        if (isClose) {
            binding.appbar.btnClose.visibility = View.VISIBLE
        } else {
            binding.appbar.btnClose.visibility = View.GONE
        }

        if (isBack) {
            binding.appbar.btnBack.visibility = View.VISIBLE
        } else {
            binding.appbar.btnBack.visibility = View.GONE
        }

        if (isClear) {
            binding.appbar.btnClear.visibility = View.VISIBLE
        } else {
            binding.appbar.btnClear.visibility = View.GONE
        }

        if (textDescription.isEmpty() || textDescription.isBlank()) {
            binding.appbar.description.visibility = View.GONE
        } else {
            binding.appbar.description.visibility = View.VISIBLE
        }

        binding.appbar.btnBack.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        binding.appbar.title.text = textTitle
        binding.appbar.description.text = textDescription

    }
}