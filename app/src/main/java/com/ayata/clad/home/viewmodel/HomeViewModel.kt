package com.ayata.clad.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import retrofit2.http.Query

class HomeViewModel constructor(private val mainRepository: ApiRepository)  : ViewModel(){

    val errorMessage = MutableLiveData<String>()

    private val homeResponse = MutableLiveData<Resource<JsonObject>>()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun dashboardAPI() {
        homeResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.dashboardAPI()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("homeResponse", "login: "+response.body().toString())
                    homeResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("homeResponse", "error: $response")
                    onError("Error : ${response.message()} ")
                    homeResponse.postValue(Resource.error("Something Went Wrong", null))
                }
            }
        }

    }

    fun getDashboardAPI(): LiveData<Resource<JsonObject>> {
        return homeResponse
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