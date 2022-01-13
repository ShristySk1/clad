package com.ayata.clad.shopping_bag.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayata.clad.data.repository.ApiRepository

class ShoppingBagViewModelFactory constructor(private val repository: ApiRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ShoppingBagViewModel::class.java)) {
            ShoppingBagViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}