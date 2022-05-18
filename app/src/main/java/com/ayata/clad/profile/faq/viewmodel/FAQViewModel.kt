package com.ayata.clad.profile.faq.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.profile.edit.response.Details
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.SingleLiveEvent
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class FAQViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    private val errorMessage = MutableLiveData<String>()

    private val faqResponse = MutableLiveData<Resource<JsonObject>>()


    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()

    fun getFAQAPI(token: String) {
        faqResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.getFAQ("$token")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("profileDetailAPI", "success: " + response.body())
                        faqResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("profileDetailAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        faqResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            }catch (e:Exception){
                faqResponse.postValue(Resource.error(e.message.toString(), null))
            }


        }

    }
    fun observerGetFAQAPI(): LiveData<Resource<JsonObject>> {
        return faqResponse
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