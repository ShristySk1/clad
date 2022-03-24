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

class OrderViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    private val errorMessage = MutableLiveData<String>()

    private val orderResponse = MutableLiveData<Resource<JsonObject>>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()

    fun getOrderApi(token: String) {
        orderResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
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
        }

    }


    fun observeOrderResponse(): LiveData<Resource<JsonObject>> {
        return orderResponse
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