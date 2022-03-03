package com.ayata.clad.productlist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.utils.Constants
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class ProductListViewModel constructor(private val mainRepository: ApiRepository)  : ViewModel(){

    val errorMessage = MutableLiveData<String>()

    private val listResponse = MutableLiveData<Resource<JsonObject>>()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun productListApi(token:String) {
        listResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.productListApi("${Constants.Bearer} $token")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("productListApi", "success: "+response.body())
                    listResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("productListApi", "error: $response")
                    onError("Error : ${response.message()} ")
                    listResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getProductListAPI(): LiveData<Resource<JsonObject>> {
        return listResponse
    }
    var currentPage = 1
    private var shouldFetchAgain: Boolean
    init {
        shouldFetchAgain = true
        Log.d("initcalled", ": calledinit ");
    }
    private val categoryProductResponse = MutableLiveData<Resource<JsonObject>>()
    fun categoryProductListAPI(categoryId: Int) {

        if (shouldFetchAgain) {
        categoryProductResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.categoryProductListAPI(categoryId,currentPage)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    currentPage++
                    Log.d("categoryListAPI", "success: "+response.body())
                    categoryProductResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                    shouldFetchAgain=false
                } else {
                    Log.e("categoryListAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    shouldFetchAgain=false
                    categoryProductResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }
    }}
    fun getCategoryProductListAPI(): LiveData<Resource<JsonObject>> {
        return categoryProductResponse
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