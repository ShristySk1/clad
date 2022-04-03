package com.ayata.clad.profile.giftcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayata.clad.data.repository.ApiRepository

class GiftcardViewModelFactory constructor(private val repository: ApiRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(GiftcardViewModel::class.java)) {
            GiftcardViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}