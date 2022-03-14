package com.ayata.clad.profile.address.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayata.clad.data.repository.ApiRepository

class AddressViewModelFactory constructor(private val repository: ApiRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddressViewModel::class.java)) {
            AddressViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}