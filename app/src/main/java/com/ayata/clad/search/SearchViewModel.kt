package com.ayata.clad.search

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.SingleLiveEvent
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class SearchViewModel constructor(private val mainRepository: ApiRepository)  : ViewModel(){

    val errorMessage = MutableLiveData<String>()

    private val listResponse = MutableLiveData<Resource<JsonObject>>()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun productListSearchApi(token:String) {
        listResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.searchOrder("$token","",1)
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

    fun getProductListSearchAPI(): LiveData<Resource<JsonObject>> {
        return listResponse
    }
    private val currentQuery = MutableLiveData(DEFAULT_QUERY)
    private var auth=""

    val productList: LiveData<PagingData<ProductDetail>> = currentQuery.switchMap { queryString ->
        mainRepository.getSearchResult(queryString, auth = auth).cachedIn(viewModelScope)
    }
    fun searchProduct(query: String, autho: String) {
        currentQuery.value = query
        auth=autho
    }
//    fun fetchProductsViewAllLiveData(query: String, auth: String): LiveData<PagingData<ProductDetail>> {
//        return mainRepository.getSearchResult(query,auth)
//            .cachedIn(viewModelScope)
//    }
//private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = ""
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