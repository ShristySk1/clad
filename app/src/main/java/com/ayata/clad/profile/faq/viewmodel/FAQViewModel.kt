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
    private val addFaqResponse = MutableLiveData<Resource<JsonObject>>()
    private val deleteResponse = MutableLiveData<Resource<JsonObject>>()


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
                        Log.d("faqresponse", "success: " + response.body())
                        faqResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("faqresponse", "error: $response")
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

    fun addFAQAPI(jsonObject: JsonObject) {
        addFaqResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.addFaqQuestion(jsonObject)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("faqresponse", "success: " + response.body())
                        addFaqResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("faqresponse", "error: $response")
                        onError("Error : ${response.message()} ")
                        addFaqResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            }catch (e:Exception){
                addFaqResponse.postValue(Resource.error(e.message.toString(), null))
            }


        }

    }
    fun observerAddFAQAPI(): LiveData<Resource<JsonObject>> {
        return addFaqResponse
    }

    fun deleteFAQAPI(questionId: Int) {
        deleteResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.deleteFaqQuestion(questionId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("faqresponse", "success: " + response.body())
                        deleteResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("faqresponse", "error: $response")
                        onError("Error : ${response.message()} ")
                        deleteResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            }catch (e:Exception){
                deleteResponse.postValue(Resource.error(e.message.toString(), null))
            }
        }

    }
    fun observerDeleteFAQAPI(): LiveData<Resource<JsonObject>> {
        return deleteResponse
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