package com.ayata.clad.home.viewmodel

import android.util.JsonToken
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

    private val errorMessage = MutableLiveData<String>()

    private val homeResponse = MutableLiveData<Resource<JsonObject>>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()

    fun dashboardAPI(token: String) {
        homeResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try{
                val response = mainRepository.dashboardAPI(token)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("homeResponse", "success: "+response.body())
                        homeResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("homeResponse", "error: $response")
                        onError("Error : ${response.message()} ")
                        homeResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            }catch (e:Exception){
                homeResponse.postValue(Resource.error(e.message.toString(), null))

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