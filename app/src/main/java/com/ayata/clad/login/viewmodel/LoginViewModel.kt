package com.ayata.clad.login.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class LoginViewModel constructor(private val mainRepository: ApiRepository)  : ViewModel(){

    val errorMessage = MutableLiveData<String>()
    val movieList = MutableLiveData<JsonObject>()

    private val loginResponse = MutableLiveData<Resource<JsonObject>>()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun phoneAPI(phone:String) {
        loginResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.phoneAPI(phone)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("phoneResponse", "login: "+response.body().toString())
                    loginResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("phoneResponse", "error: $response")
                    onError("Error : ${response.message()} ")
                    loginResponse.postValue(Resource.error("Something Went Wrong", null))
                }
            }
        }

    }


    fun otpVerification(phone: String,otp:String) {
        loginResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.otpVerification(phone,otp)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("otpResponse", "otp: "+response.body().toString())
                    loginResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("otpResponse", "error: $response")
                    onError("Error : ${response.message()} ")
                    loginResponse.postValue(Resource.error("Something Went Wrong", null))
                }
            }
        }

    }

    fun doPhone(): LiveData<Resource<JsonObject>> {
        return loginResponse
    }


    fun doOTPCheck(): LiveData<Resource<JsonObject>> {
        return loginResponse
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