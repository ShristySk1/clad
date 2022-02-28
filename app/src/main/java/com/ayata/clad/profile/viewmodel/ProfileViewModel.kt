package com.ayata.clad.profile.viewmodel

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

class ProfileViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    private val errorMessage = MutableLiveData<String>()

    private val profileResponse = MutableLiveData<Resource<JsonObject>>()
    private val profileUpdateResponse = MutableLiveData<Resource<JsonObject>>()
    private val orderResponse = MutableLiveData<Resource<JsonObject>>()
    private val orderDetailResponse = MutableLiveData<Resource<JsonObject>>()
    private val profileDetail = MutableLiveData<Details>()


    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()

    fun profileDetailAPI(token: String) {
        profileResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.profileApi("${Constants.Bearer} $token")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("profileDetailAPI", "success: " + response.body())
                    profileResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("profileDetailAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    profileResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun profileDetailUpdateAPI(token: String, profile: Details) {
        profileUpdateResponse.postValue(Resource.loading(null))
        try {
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                val response =
                    mainRepository.profileUpdateApi("${Constants.Bearer} $token", profile)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("profileDetailAPI", "success: " + response.body())
                        profileUpdateResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("profileDetailAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        profileUpdateResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            }

        } catch (e: Exception) {
            profileUpdateResponse.postValue(Resource.error(e.message!!, null))

        }
    }

    fun getProfileAPI(): LiveData<Resource<JsonObject>> {
        return profileResponse
    }

    fun postProfileAPI(): LiveData<Resource<JsonObject>> {
        return profileUpdateResponse
    }

    fun orderListAPI(token: String) {
        orderResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.orderListApi("${Constants.Bearer} $token")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("orderListAPI", "success: " + response.body())
                    orderResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("orderListAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    orderResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getOrderListAPI(): LiveData<Resource<JsonObject>> {
        return orderResponse
    }

    fun orderDetailAPI(token: String, id: Int) {
        orderDetailResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.orderDetailApi("${Constants.Bearer} $token", id)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("orderDetailAPI", "success: " + response.body())
                    orderDetailResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("orderDetailAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    orderDetailResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getOrderDetailAPI(): LiveData<Resource<JsonObject>> {
        return orderDetailResponse
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