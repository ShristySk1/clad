package com.ayata.clad.profile.faq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayata.clad.data.repository.ApiRepository

class FAQViewModelFactory constructor(private val repository: ApiRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FAQViewModel::class.java)) {
            FAQViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}