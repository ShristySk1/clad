package com.ayata.clad.product.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.utils.SingleLiveEvent
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import org.json.JSONObject

class ProductViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    private val errorMessage = MutableLiveData<String>()

    private val removeWishResponse = MutableLiveData<Resource<JsonObject>>()
    private val addWishResponse = SingleLiveEvent<Resource<JsonObject>>()
    private val removeCartResponse = MutableLiveData<Resource<JsonObject>>()
    private val addCartResponse = SingleLiveEvent<Resource<JsonObject>>()
    private var productResponse = MutableLiveData<Resource<JsonObject>>()


    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()


    fun addToCartAPI(token: String, id: Int) {
        addCartResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            try {
                val response = mainRepository.addToCartApi("$token", id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("addToCartAPI", "success: " + response.body())
                        addCartResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else if (response.code() == 400) {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val message = jObjError.getString("message")
                            onError("Error : ${message} ")
                            addCartResponse.postValue(Resource.error(message, null))
                        } catch (e: java.lang.Exception) {
                            addCartResponse.postValue(Resource.error(e.message.toString(), null))
                        }
                    } else {
                        onError("Error : ${response.message()} ")
                        addCartResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            } catch (e: Exception) {
                addCartResponse.postValue(Resource.error(e.message.toString(), null))
            }

        }

    }

    fun getAddToCartAPI(): SingleLiveEvent<Resource<JsonObject>> {
        return addCartResponse
    }

    fun removeFromWishAPI(token: String, id: Int) {
        removeWishResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("variant_id", id)
            val response = mainRepository.removeFromWishAPI("$token", jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("removeFromWishAPI", "success: " + response.body())
                    removeWishResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("removeFromWishAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    removeWishResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getRemoveFromWishAPI(): LiveData<Resource<JsonObject>> {
        return removeWishResponse
    }

    fun addToWishAPI(token: String, id: Int) {
        addWishResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val jsonObject = JsonObject()
                jsonObject.addProperty("variant_id", id)
                val response = mainRepository.addToWishApi("$token", jsonObject)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("addToWishAPI", "success: " + response.body())
                        addWishResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("addToWishAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        addWishResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            } catch (e: Exception) {
                addWishResponse.postValue(Resource.error(e.message.toString(), null))

            }

        }

    }

    fun getAddToWishAPI(): SingleLiveEvent<Resource<JsonObject>> {
        return addWishResponse
    }

    fun productAPI(token: String, id: Int) {
        productResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.getProduct(token, id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("removeFromCartAPI", "success: " + response.body())
                        productResponse.postValue(Resource.success(response.body()))
                        loading.value = false
//                    productResponse=(MutableLiveData<Resource<JsonObject>>())
                    } else {
                        Log.e("removeFromCartAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        productResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            } catch (e: Exception) {
                productResponse.postValue(Resource.error(e.message.toString(), null))
            }

        }
    }

    fun getProductAPI(): MutableLiveData<Resource<JsonObject>> {
        return productResponse
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