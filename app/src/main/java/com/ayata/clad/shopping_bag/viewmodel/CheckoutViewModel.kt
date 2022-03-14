package com.ayata.clad.shopping_bag.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.utils.Constants
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class CheckoutViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    private val errorMessage = MutableLiveData<String>()

    private val cartResponse = MutableLiveData<Resource<JsonObject>>()
    private val removeCartResponse = MutableLiveData<Resource<JsonObject>>()
    private val minusCartResponse = MutableLiveData<Resource<JsonObject>>()
    private val addCartResponse = MutableLiveData<Resource<JsonObject>>()
    private val sizeResponse = MutableLiveData<Resource<JsonObject>>()
    private val quantityResponse = MutableLiveData<Resource<JsonObject>>()
    private val cartSelectResponse = MutableLiveData<Resource<JsonObject>>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()

    fun cartListAPI(token: String) {
        cartResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.cartListApi("${Constants.Bearer} $token")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("cartListAPI", "success: " + response.body())
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

    fun removeFromCartAPI(token: String, id: Int) {
        removeCartResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("cart_id", id)
            val response =
                mainRepository.removeFromCartAPI("${Constants.Bearer} $token", jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("removeFromCartAPI", "success: " + response.body())
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

    fun minusFromCartAPI(token: String, id: Int) {
        minusCartResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("cart_id", id)
            val response = mainRepository.minusFromCartAPI("${Constants.Bearer} $token", jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("removeFromCartAPI", "success: " + response.body())
                    minusCartResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("removeFromCartAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    minusCartResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getMinusFromCartAPI(): LiveData<Resource<JsonObject>> {
        return minusCartResponse
    }

    fun selectCartApi(token: String, id: Int) {
        cartSelectResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.cartsSeletApi("${Constants.Bearer} $token", id)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("removeFromCartAPI", "success: " + response.body())
                    cartSelectResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("removeFromCartAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    cartSelectResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }
    }
    fun getSelectCartAPI(): LiveData<Resource<JsonObject>> {
        return cartSelectResponse
    }

    fun addToCartAPI(token: String, id: Int) {
        addCartResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
Log.d("getcartid", "addToCartAPI: "+id);
            val response = mainRepository.addToCartApi("${Constants.Bearer} $token", id)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("addToCartAPIsuccess", "success: " + response.body())
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
    fun resetAddCartLiveData(){
        addCartResponse.value=null
    }

    fun saveSizeAPI(token: String, id: Int, size: String) {
        sizeResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("product_id", id)
            jsonObject.addProperty("size", id)
            val response = mainRepository.saveSizeAPI("${Constants.Bearer} $token", jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("saveSizeAPI", "success: " + response.body())
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

    fun saveQuantityAPI(token: String, id: Int, quantity: String) {
        quantityResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("product_id", id)
            jsonObject.addProperty("quantity", id)
            val response = mainRepository.saveSizeAPI("${Constants.Bearer} $token", jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("saveQuantityAPI", "success: " + response.body())
                    quantityResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("saveQuantityAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    quantityResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getQuantityAPI(): LiveData<Resource<JsonObject>> {
        return quantityResponse
    }

    private fun onError(message: String) {
        errorMessage.postValue(message)
        loading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}