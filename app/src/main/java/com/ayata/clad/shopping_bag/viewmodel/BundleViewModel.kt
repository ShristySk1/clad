package com.ayata.clad.shopping_bag.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.shopping_bag.response.checkout.Cart
import com.ayata.clad.utils.Constants
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class BundleViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    private val cartList = MutableLiveData<List<Cart>>()
    private val addressId=MutableLiveData<Int>()

    fun setCart(carts:ArrayList<Cart>){
        cartList.value=carts
    }
    fun getCart():LiveData<List<Cart>>{
        return cartList
    }
    fun setAddressId(id: Int){
        addressId.value=id
    }
    fun getAddressId():LiveData<Int>{
        return addressId
    }
}