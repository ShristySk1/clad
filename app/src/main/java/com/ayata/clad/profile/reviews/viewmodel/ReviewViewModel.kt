package com.ayata.clad.profile.reviews.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.lang.Exception

class ReviewViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    private val errorMessage = MutableLiveData<String>()
    private val reviewResponse = MutableLiveData<Resource<JsonObject>>()
    private val postReviewResponse = MutableLiveData<Resource<JsonObject>>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()

    fun reviewAPI(token: String) {
        reviewResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.getReviewApi("$token")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("profileDetailAPI", "success: " + response.body())
                    reviewResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("profileDetailAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    reviewResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }
    }

    fun observeGetReviewApi(): LiveData<Resource<JsonObject>> {
        return reviewResponse
    }
    fun postReviewAPI(
        token: String,
        desc: RequestBody,
        rate: Float,
        orderId: Int,
        images: List<MultipartBody.Part>,
        size: RequestBody,
        comfort: RequestBody,
        quality: Int
    ) {
        postReviewResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.postReviewApi("$token",desc,rate,orderId, images,size,comfort,quality)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("profileDetailAPI", "success: " + response.body())
                        postReviewResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("profileDetailAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        postReviewResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            }catch (e:Exception){
                Log.d("testerror", "postReviewAPI: "+e.message);
                postReviewResponse.postValue(Resource.error(e.message.toString(), null))

            }

        }
    }

    fun observePostReviewApi(): LiveData<Resource<JsonObject>> {
        return postReviewResponse
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