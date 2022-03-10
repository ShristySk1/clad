package com.ayata.clad.wishlist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.utils.Constants
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import retrofit2.http.Query

class WishListViewModel constructor(private val mainRepository: ApiRepository)  : ViewModel(){

    private val errorMessage = MutableLiveData<String>()

    private val listResponse = MutableLiveData<Resource<JsonObject>>()
    private val removeResponse = MutableLiveData<Resource<JsonObject>>()
    private val addCartResponse = MutableLiveData<Resource<JsonObject>>()
    private val sizeResponse = MutableLiveData<Resource<JsonObject>>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()

    fun wishListAPI(token:String) {
        listResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.wishListApi("${Constants.Bearer} $token")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("wishListAPI", "success: "+response.body())
                    listResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("wishListAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    listResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getWishListAPI(): LiveData<Resource<JsonObject>> {
        return listResponse
    }

    fun removeFromWishAPI(token:String,id: Int) {
        removeResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject=JsonObject()
            jsonObject.addProperty("wishlist_id",id)
            val response = mainRepository.removeFromWishAPI("${Constants.Bearer} $token",jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("removeFromWishAPI", "success: "+response.body())
                    removeResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("removeFromWishAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    removeResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getRemoveFromWishAPI(): LiveData<Resource<JsonObject>> {
        return removeResponse
    }
    private val wishListToCart = MutableLiveData<Resource<JsonObject>>()
    fun wishListToCart(token: String, id: Int) {
        wishListToCart.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.wishlistToCart("${Constants.Bearer} $token", id)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("addToWishAPI", "success: " + response.body())
                    wishListToCart.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("addToWishAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    wishListToCart.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getWishAPIToCart(): LiveData<Resource<JsonObject>> {
        return wishListToCart
    }
    fun addToCartAPI(token:String,id:Int) {
        addCartResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject=JsonObject()
            jsonObject.addProperty("product_id",id)
            jsonObject.addProperty("quantity",1)

            val response = mainRepository.addToCartApi("${Constants.Bearer} $token",jsonObject)
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

    fun saveSizeAPI(token:String,id: Int,size:String) {
        sizeResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject=JsonObject()
            jsonObject.addProperty("product_id",id)
            jsonObject.addProperty("size",id)
            val response = mainRepository.saveSizeAPI("${Constants.Bearer} $token",jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("saveSizeAPI", "success: "+response.body())
                    sizeResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("saveSizeAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    sizeResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getSizeAPI(): LiveData<Resource<JsonObject>> {
        return sizeResponse
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