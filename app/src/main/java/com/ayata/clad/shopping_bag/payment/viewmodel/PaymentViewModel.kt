package com.ayata.clad.shopping_bag.payment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.shopping_bag.model.ModelFinalOrder
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import org.json.JSONArray





class PaymentViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    private val errorMessage = MutableLiveData<String>()
    private val paymentResponse = MutableLiveData<Resource<JsonObject>>()
    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()

    fun getPaymentApi(token: String) {
        paymentResponse.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.getPaymentGateways("$token")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("cartListAPI", "success: " + response.body())
                        paymentResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("cartListAPI", "error: $response")
                        onError("Error : ${response.message()} ")
                        paymentResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            } catch (e: Exception) {
                paymentResponse.postValue(Resource.error(e.message.toString(), null))
            }
        }
    }

    fun observePaymentApi(): LiveData<Resource<JsonObject>> {
        return paymentResponse
    }

    private val checkoutResponse = MutableLiveData<Resource<JsonObject>>()

    //checkout after payment
    fun checkoutOrder(
        token: String,
        paymentID: String,
        payment_token: String,
        cartList: List<Int>,
        addressId: Int,
        addressType: String,
        amountPaid: Double
    ) {
        checkoutResponse.postValue(Resource.loading(null))
        val finalOrder = ModelFinalOrder(
            payment_gateway = paymentID,
            payment_token = payment_token,
            received_amount = amountPaid, cart_id = cartList,
            address_id = addressId,
            address_type = addressType
        )
        val s=Gson().toJson(finalOrder).toString()
        Log.d("testdata1", "checkoutOrder: "+s);
        Log.d("testdata", "checkoutOrder: "+s.replace("\\\\",""));
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val jsonObject=JsonObject()
                jsonObject.addProperty("payment_gateway",paymentID)
                jsonObject.addProperty("payment_token",payment_token)
                jsonObject.addProperty("received_amount",amountPaid)
                jsonObject.add("cart_id",Gson().toJsonTree(cartList).getAsJsonArray())
                jsonObject.addProperty("address_id",addressId)
                jsonObject.addProperty("address_type",addressType)
                val response =
                    mainRepository.checkoutOrder("$token",jsonObject )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("checkoutorder", "success: " + response.body())
                        checkoutResponse.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("checkoutorder", "error: $response")
                        onError("Error : ${response.message()} ")
                        checkoutResponse.postValue(Resource.error(response.message(), null))
                    }
                }
            } catch (e: Exception) {
                checkoutResponse.postValue(Resource.error(e.message.toString(), null))
            }
        }
    }

    fun observeCheckoutOrder(): LiveData<Resource<JsonObject>> {
        return checkoutResponse
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