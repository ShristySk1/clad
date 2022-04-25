package com.ayata.clad.productlist.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.home.response.ProductDetail
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import java.util.*


class ProductListViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()


    private val categoryProductResponse = MutableLiveData<Resource<JsonObject>>()
    private val currentQuery = MutableLiveData(DEFAULT_QUERY)
    private val testQuery = MutableLiveData<String>()
    private var myViewmodelToken: String = ""
    private var size: String = ""
    private var brand: String = ""
    private var color: String = ""
    private var sortBy: String = ""
    private var priceFrom: String = ""
    private var priceTo: String = ""
    fun setQuesry(string: String) {
        testQuery.value = string
    }

    fun getQuesry() = testQuery.value

    val productList: LiveData<PagingData<ProductDetail>> = currentQuery.switchMap { categoryId ->
        val data: MutableMap<String, String> = HashMap()
        if (size.isNotEmpty())
            data["size"] = size
        if (brand.isNotEmpty())
            data["brand"] = brand
        if (color.isNotEmpty())
            data["color"] = color
        if (sortBy.isNotEmpty())
            data["sort_by"] = sortBy
        if (priceFrom.isNotEmpty())
            data["price_from"] = priceFrom
        if (priceTo.isNotEmpty())
            data["price_to"] = priceTo
        if (categoryId.isNotEmpty())
            data["category"] = categoryId
        mainRepository.searchProductListFromCategory(
            myViewmodelToken,
            data
        ).cachedIn(viewModelScope)
    }

    //productlist
    fun searchProductListFromCategory(
        token: String,
        category_slug: String,
        msize: String,
        mbrand: String,
        mcolor: String,
        msortBy: String,
        mpriceFrom: String,
        mpriceTo: String
    ) {
        myViewmodelToken = token
        size = msize
        brand = mbrand
        color = mcolor
        sortBy = msortBy
        priceFrom = mpriceFrom
        priceTo = mpriceTo
        currentQuery.value = category_slug
        Log.d("current", "searchProductListFromCategory: " + currentQuery.value);
    }

    fun getCurrent() {
        Log.d("testcurrent", "getCurrent: " + currentQuery.value);
    }

    companion object {
        private const val CURRENT_QUERY = 1
        private const val DEFAULT_QUERY = ""
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