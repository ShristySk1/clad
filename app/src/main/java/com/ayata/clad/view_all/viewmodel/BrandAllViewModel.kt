package com.ayata.clad.view_all.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class BrandAllViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()

    private val listResponse = MutableLiveData<Resource<JsonObject>>()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun brandListApi(filter: String) {
        listResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.brandListApi(1, filter)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("brandListApi", "success: " + response.body())
                    listResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("brandListApi", "error: $response")
                    onError("Error : ${response.message()} ")
                    listResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getBrandListAPI(): LiveData<Resource<JsonObject>> {
        return listResponse
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