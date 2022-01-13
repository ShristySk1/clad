package com.ayata.clad.shopping_bag.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class ShoppingBagViewModel constructor(private val mainRepository: ApiRepository)  : ViewModel(){

    private val errorMessage = MutableLiveData<String>()

    private val cartResponse = MutableLiveData<Resource<JsonObject>>()
    private val removeCartResponse = MutableLiveData<Resource<JsonObject>>()
    private val addCartResponse = MutableLiveData<Resource<JsonObject>>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()

    fun cartListAPI(token:String) {
        cartResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.cartListApi("Bearer $token")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("cartListAPI", "success: "+response.body())
                    cartResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("cartListAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    cartResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getCartListAPI(): LiveData<Resource<JsonObject>> {
        return cartResponse
    }

    fun removeFromCartAPI(token:String,id: Int) {
        removeCartResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject=JsonObject()
            jsonObject.addProperty("product_id",id)
            val response = mainRepository.removeFromCartAPI("Bearer $token",jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("removeFromCartAPI", "success: "+response.body())
                    removeCartResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("removeFromCartAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    removeCartResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getRemoveFromCartAPI(): LiveData<Resource<JsonObject>> {
        return removeCartResponse
    }

    fun addToCartAPI(token:String,id:Int) {
        addCartResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject=JsonObject()
            jsonObject.addProperty("product_id",id)
            val response = mainRepository.addToCartApi("Bearer $token",jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("addToCartAPI", "success: "+response.body())
                    addCartResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("addToCartAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    addCartResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getAddToCartAPI(): LiveData<Resource<JsonObject>> {
        return addCartResponse
    }

    private fun onError(message: String) {
        errorMessage.postValue( message)
        loading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}