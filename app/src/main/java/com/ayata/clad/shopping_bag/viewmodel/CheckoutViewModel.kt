package com.ayata.clad.shopping_bag.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.utils.SingleLiveEvent
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import org.json.JSONObject


class CheckoutViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    private val errorMessage = MutableLiveData<String>()

    private val cartResponse = MutableLiveData<Resource<JsonObject>>()
    private val removeCartResponse = SingleLiveEvent<Resource<JsonObject>>()
    private val minusCartResponse = SingleLiveEvent<Resource<JsonObject>>()
    private val addCartResponse = SingleLiveEvent<Resource<JsonObject>>()
    private val sizeResponse = MutableLiveData<Resource<JsonObject>>()
    private val quantityResponse = MutableLiveData<Resource<JsonObject>>()
    private val cartSelectResponse = SingleLiveEvent<Resource<JsonObject>>()
    private val applyCouppnResponse = SingleLiveEvent<Resource<JsonObject>>()
    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()

    fun cartListAPI(token: String) {
        cartResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.cartListApi("$token")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("cartListAPI", "success: " + response.body())
                        cartResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("cartListAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        cartResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            } catch (e: Exception) {
                cartResponse.postValue(Resource.error(e.message.toString(), null))

            }

        }

    }

    fun getCartListAPI(): LiveData<Resource<JsonObject>> {
        return cartResponse
    }

    fun removeFromCartAPI(token: String, id: Int) {
        removeCartResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {

                val jsonObject = JsonObject()
                jsonObject.addProperty("cart_id", id)
                val response =
                    mainRepository.removeFromCartAPI("$token", jsonObject)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("removeFromCartAPI", "success: " + response.body())
                        removeCartResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("removeFromCartAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        removeCartResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            } catch (e: Exception) {
                removeCartResponse.postValue(Resource.error(e.message.toString(), null))

            }

        }

    }

    fun getRemoveFromCartAPI(): SingleLiveEvent<Resource<JsonObject>> {
        return removeCartResponse
    }

    fun minusFromCartAPI(token: String, id: Int) {
        minusCartResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            try {
                val jsonObject = JsonObject()
                jsonObject.addProperty("cart_id", id)
                val response = mainRepository.minusFromCartAPI("$token", jsonObject)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("removeFromCartAPI", "success: " + response.body())
                        minusCartResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("removeFromCartAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        minusCartResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            } catch (e: Exception) {
                minusCartResponse.postValue(Resource.error(e.message.toString(), null))

            }

        }

    }

    fun getMinusFromCartAPI(): SingleLiveEvent<Resource<JsonObject>> {
        return minusCartResponse
    }

    fun selectCartApi(token: String, id: Int) {
        cartSelectResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.cartsSeletApi("$token", id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("removeFromCartAPI", "success: " + response.body())
                        cartSelectResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("removeFromCartAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        cartSelectResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            } catch (e: Exception) {
                cartSelectResponse.postValue(Resource.error(e.message.toString(), null))

            }

        }
    }

    fun getSelectCartAPI(): SingleLiveEvent<Resource<JsonObject>> {
        return cartSelectResponse
    }

    fun addToCartAPI(token: String, id: Int) {
        addCartResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                Log.d("getcartid", "addToCartAPI: " + id);
                val response = mainRepository.addToCartApi("$token", id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("addToCartAPIsuccess", "success: " + response.body())
                        addCartResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("addToCartAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        addCartResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            } catch (e: Exception) {
                addCartResponse.postValue(Resource.error(e.message.toString(), null))
            }
        }

    }

    fun getAddToCartAPI(): SingleLiveEvent<Resource<JsonObject>> {
        return addCartResponse
    }


    fun saveSizeAPI(token: String, id: Int, size: String) {
        sizeResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("product_id", id)
            jsonObject.addProperty("size", id)
            val response = mainRepository.saveSizeAPI("$token", jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("saveSizeAPI", "success: " + response.body())
                    sizeResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("saveSizeAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    sizeResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getSizeAPI(): LiveData<Resource<JsonObject>> {
        return sizeResponse
    }

    fun saveQuantityAPI(token: String, id: Int, quantity: String) {
        quantityResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("product_id", id)
            jsonObject.addProperty("quantity", id)
            val response = mainRepository.saveSizeAPI("$token", jsonObject)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("saveQuantityAPI", "success: " + response.body())
                    quantityResponse.postValue(Resource.success(response.body()))
                    loading.value = false
                } else {
                    Log.e("saveQuantityAPI", "error: $response")
                    onError("Error : ${response.message()} ")
                    quantityResponse.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }

    fun getQuantityAPI(): LiveData<Resource<JsonObject>> {
        return quantityResponse
    }
    fun applyCouponAPI(token: String,coupon_code:String) {
        applyCouppnResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.applyCoupons("$token",coupon_code)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("cartListAPI", "success: " + response.body())
                        applyCouppnResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string()).get("message").toString()
                            applyCouppnResponse.postValue(Resource.error(jObjError, null))
                        } catch (e: java.lang.Exception) {
                            applyCouppnResponse.postValue(Resource.error("Parse error 400", null))
                        }
                        onError("Error : ${response.message()} ")
                    }
                }
            } catch (e: Exception) {
                applyCouppnResponse.postValue(Resource.error(e.message.toString(), null))

            }

        }

    }

    fun getApplyCouponResponseAPI(): SingleLiveEvent<Resource<JsonObject>> {
        return applyCouppnResponse
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