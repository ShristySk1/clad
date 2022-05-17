package com.ayata.clad.login.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class LoginViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    private val errorMessage = MutableLiveData<String>()
    private val loginResponse = MutableLiveData<Resource<JsonObject>>()
    private val loginGoogleResponse = MutableLiveData<Resource<JsonObject>>()
    private val logoutResponse = MutableLiveData<Resource<JsonObject>>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()

    fun phoneAPI(phone: String) {
        loginResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("phone_no", phone)
            Log.d("phoneResponse", "login: $jsonObject")
            val response = mainRepository.phoneAPI(jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("phoneResponse", "login: " + response.body().toString())
                    loginResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("phoneResponse", "error: $response")
                    onError("Error : ${response.message()} ")
                    loginResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }


    fun otpVerification(phone: String, otp: String) {
        loginResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("phone_no", phone)
            jsonObject.addProperty("otp_code", otp)
            val response = mainRepository.otpVerification(jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("otpResponse", "otp: " + response.body().toString())
                    loginResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("otpResponse", "error: $response")
                    onError("Error : ${response.message()} ")
                    loginResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun login(token: String,code:String) {
        loginGoogleResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.login(token,code)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("otpResponse", "otp: " + response.body().toString())
                        loginGoogleResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("otpResponse", "error: $response")
                        onError("Error : ${response.message()} ")
                        loginGoogleResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            } catch (e: Exception) {
                Log.d("test;logim", "login: " + e.message);
                loginGoogleResponse.postValue(Resource.error(e.message ?: "", null))
            }
        }


    }

    fun doLogin(): LiveData<Resource<JsonObject>> {
        return loginGoogleResponse
    }

    //logout
    fun logout(token: String) {
        logoutResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.logout(token)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("otpResponse", "otp: " + response.body().toString())
                    logoutResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("otpResponse", "error: $response")
                    onError("Error : ${response.message()} ")
                    logoutResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun resendOtpAPI(phone: String) {
        loginResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("phone_no", phone)
            Log.d("phoneResponse", "login: $jsonObject")
            val response = mainRepository.resendOtpAPI(jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("resendOtpAPI", "success: " + response.body().toString())
                    loginResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("resendOtpAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    loginResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun doPhone(): LiveData<Resource<JsonObject>> {
        return loginResponse
    }

    fun dologout(): LiveData<Resource<JsonObject>> {
        return logoutResponse
    }

    fun doOTPCheck(): LiveData<Resource<JsonObject>> {
        return loginResponse
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