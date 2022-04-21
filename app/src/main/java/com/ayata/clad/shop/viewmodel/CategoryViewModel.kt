package com.ayata.clad.shopping_bag.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.utils.SingleLiveEvent
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class CategoryViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    private val errorMessage = MutableLiveData<String>()

    private val categoryResponse = MutableLiveData<Resource<JsonObject>>()
    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()
    fun categoryListAPI() {
        categoryResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.categoryListAPI()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("categoryListAPI", "success: " + response.body())
                        categoryResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("categoryListAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        categoryResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            }catch (e:Exception){
                categoryResponse.postValue(Resource.error(e.message.toString(), null))

            }

        }

    }

    fun getCategoryListAPI(): MutableLiveData<Resource<JsonObject>> {
        return categoryResponse
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