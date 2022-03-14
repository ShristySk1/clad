package com.ayata.clad.profile.address.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.data.network.Resource
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.profile.address.response.ShippingAddressResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class AddressViewModel constructor(private val mainRepository: ApiRepository) : ViewModel() {

    private val errorMessage = MutableLiveData<String>()
    private val addressResposne = MutableLiveData<Resource<ShippingAddressResponse>>()
    private val addAddressResposne = MutableLiveData<Resource<JsonObject>>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()

    fun addAddress(token: String,jsonObject:JsonObject) {
        addAddressResposne.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.addAddress(token,jsonObject)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("phoneResponse", "resposne: " + response.body().toString())
                        addAddressResposne.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("phoneResponse", "error: $response")
                        onError("Error : ${response.message()} ")
                        addAddressResposne.postValue(Resource.error(response.message(), null))
                    }
                }
            }catch (e:Exception){
                addAddressResposne.postValue(Resource.error(e.message.toString(), null))

            }

        }

    }
    fun observeAddAddress(): LiveData<Resource<JsonObject>> {
        return addAddressResposne
    }

    //get address
    fun getAddress(token: String) {
        addressResposne.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.getAddress(token)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("phoneResponse", "login: " + response.body().toString())
                    addressResposne.postValue(Resource.success(response.body() as ShippingAddressResponse))
                    loading.value = false
                } else {
                    Log.e("phoneResponse", "error: $response")
                    onError("Error : ${response.message()} ")
                    addressResposne.postValue(Resource.error(response.message(), null))
                }
            }
        }

    }
    fun observeAddress(): LiveData<Resource<ShippingAddressResponse>> {
        return addressResposne
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