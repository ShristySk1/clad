package com.ayata.clad.profile.myorder.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.profile.edit.response.Details
import com.ayata.clad.utils.Constants
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.MultipartBody

class OrderViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    private val errorMessage = MutableLiveData<String>()

    private val orderResponse = MutableLiveData<Resource<JsonObject>>()
    private val cancelOrderResponse = MutableLiveData<Resource<JsonObject>>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()
    fun getOrderApi(token: String) {
        Log.d("testapihit", "getOrderApi: ");
        orderResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.getOrder("$token")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("profileDetailAPI", "success: " + response.body())
                        orderResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("profileDetailAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        orderResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            }catch (e:Exception){
                orderResponse.postValue(Resource.error(e.message.toString(), null))
            }

        }

    }
    fun observeOrderResponse(): LiveData<Resource<JsonObject>> {
        return orderResponse
    }
    fun cancelOrderApi(token: String,orderId:Int,comment:String,reason:String) {
        cancelOrderResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.cancelOrder("$token",orderId,comment,reason)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("profileDetailAPI", "success: " + response.body())
                    cancelOrderResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("profileDetailAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    cancelOrderResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }
    fun observeCancelOrderResponse(): LiveData<Resource<JsonObject>> {
        return cancelOrderResponse
    }

    private val imageUploadResponse = MutableLiveData<Resource<JsonObject>>()
    private val imageDeleteResponse = MutableLiveData<Resource<JsonObject>>()
    fun imageUploadAPI(images: List<MultipartBody.Part>) {
        imageUploadResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.uploadImageReviewApi(images)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("profileDetailAPI", "success: " + response.body())
                        imageUploadResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("profileDetailAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        imageUploadResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            } catch (e: Exception) {
                imageUploadResponse.postValue(Resource.error(e.message.toString(), null))
            }

        }
    }

    fun observeimageUploadAPI(): LiveData<Resource<JsonObject>> {
        return imageUploadResponse
    }

    fun imageDeleteAPI(image_id: Int) {
        imageDeleteResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.deleteImageReviewApi(image_id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("profileDetailAPI", "success: " + response.body())
                        imageDeleteResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("profileDetailAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        imageDeleteResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            } catch (e: Exception) {
                imageDeleteResponse.postValue(Resource.error(e.message.toString(), null))
            }

        }
    }

    fun observeDeleteUploadAPI(): LiveData<Resource<JsonObject>> {
        return imageDeleteResponse
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