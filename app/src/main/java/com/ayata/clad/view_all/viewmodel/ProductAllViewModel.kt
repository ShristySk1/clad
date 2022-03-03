package com.ayata.clad.view_all.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.utils.Constants
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class ProductAllViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()

    private val listResponse = MutableLiveData<Resource<JsonObject>>()
    private val removeWishResponse = MutableLiveData<Resource<JsonObject>>()
    private val addWishResponse = MutableLiveData<Resource<JsonObject>>()
    var currentPage = 1
    var job: Job? = null
    private var shouldFetchAgain: Boolean
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    init {
        shouldFetchAgain = true
        Log.d("initcalled", ": calledinit ");
    }

    fun productListApi(filter: String,token: String) {
        if (shouldFetchAgain) {
            listResponse.postValue(Resource.loading(null))
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                val response =
                    mainRepository.productAllApi("${Constants.Bearer} $token", currentPage, filter)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        currentPage++
                        Log.d("productListApi", "success: " + response.body())
                        listResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                        shouldFetchAgain=false
                    } else {
                        Log.e("productListApi", "error: $response")
                        onError("Error : ${response.message()} ")
                        listResponse.postValue(Resource.error(response.message(), null))
                        shouldFetchAgain=false
                    }
                }
            }
        }
    }

    fun getProductListAPI(): LiveData<Resource<JsonObject>> {
        return listResponse
    }

    fun removeFromWishAPI(token: String, id: Int) {
        removeWishResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("wishlist_id", id)
            val response =
                mainRepository.removeFromWishAPI("${Constants.Bearer} $token", jsonObject)
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
            val jsonObject = JsonObject()
            jsonObject.addProperty("product_id", id)
            val response = mainRepository.addToWishApi("${Constants.Bearer} $token", jsonObject)
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
        }

    }

    fun getAddToWishAPI(): LiveData<Resource<JsonObject>> {
        return addWishResponse
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