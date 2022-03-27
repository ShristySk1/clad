package com.ayata.clad.view_all.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.home.response.Brand
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.utils.Constants
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class BrandAllViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    var currentPage = 1

    private val listResponse = MutableLiveData<Resource<JsonObject>>()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun brandListApi(filter: String,token:String="") {
        listResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.brandListApi("$token",currentPage, filter)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    currentPage++
                    Log.d("brandListApi", "success: " + response.body())
                    listResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("brandListApi", "error: $response")
                    onError("Error : ${response.message()} ")
                    listResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    private val currentQuery = MutableLiveData(DEFAULT_QUERY)
    private var auth=""

    val brandList: LiveData<PagingData<Brand>> = currentQuery.switchMap { queryString ->
        mainRepository.getViewAllResultBrand(queryString, auth = auth).cachedIn(viewModelScope)
    }

    fun searchBrandViewAll(query: String, autho: String) {
        currentQuery.value = query
        auth=autho
    }
    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = ""
    }

    fun getBrandListAPI(): LiveData<Resource<JsonObject>> {
        return listResponse
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