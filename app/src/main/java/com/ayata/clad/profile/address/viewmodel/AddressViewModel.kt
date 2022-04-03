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
    private val getShippingAddressResposne = MutableLiveData<Resource<ShippingAddressResponse>>()
    private val addShippingAddressResposne = MutableLiveData<Resource<JsonObject>>()

    //user address
    private val getUserAddressResposne = MutableLiveData<Resource<ShippingAddressResponse>>()
    private val addUserAddressResposne = MutableLiveData<Resource<JsonObject>>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    private val loading = MutableLiveData<Boolean>()


    //shipping
    fun addShippingAddress(token: String,jsonObject:JsonObject) {
        addShippingAddressResposne.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.addShippingAddress(token,jsonObject)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("phoneResponse", "resposne: " + response.body().toString())
                        addShippingAddressResposne.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("phoneResponse", "error: $response")
                        onError("Error : ${response.message()} ")
                        addShippingAddressResposne.postValue(Resource.error(response.message(), null))
                    }
                }
            }catch (e:Exception){
                addShippingAddressResposne.postValue(Resource.error(e.message.toString(), null))

            }

        }

    }
    fun observeShippingAddAddress(): LiveData<Resource<JsonObject>> {
        return addShippingAddressResposne
    }
    fun getShippingAddress(token: String) {
        getShippingAddressResposne.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.getShippingAddress(token)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("phoneResponse", "login: " + response.body().toString())
                        getShippingAddressResposne.postValue(Resource.success(response.body() as ShippingAddressResponse))
                        loading.value = false
                    } else {
                        Log.e("phoneResponse", "error: $response")
                        onError("Error : ${response.message()} ")
                        getShippingAddressResposne.postValue(Resource.error(response.message(), null))
                    }
                }
            }catch (e:Exception){
                getShippingAddressResposne.postValue(Resource.error(e.message.toString(), null))

            }

        }

    }
    fun observeShippingAddress(): LiveData<Resource<ShippingAddressResponse>> {
        return getShippingAddressResposne
    }

    //user
    fun addUserAddress(token: String,jsonObject:JsonObject) {
        addUserAddressResposne.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.addAddress(token,jsonObject)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("phoneResponse", "resposne: " + response.body().toString())
                        addUserAddressResposne.postValue(Resource.success(response.body()))
                        loading.value = false
                    } else {
                        Log.e("phoneResponse", "error: $response")
                        onError("Error : ${response.message()} ")
                        addUserAddressResposne.postValue(Resource.error(response.message(), null))
                    }
                }
            }catch (e:Exception){
                addUserAddressResposne.postValue(Resource.error(e.message.toString(), null))

            }

        }

    }
    fun observeUserAddAddress(): LiveData<Resource<JsonObject>> {
        return addUserAddressResposne
    }
    fun getUserAddress(token: String) {
        getUserAddressResposne.postValue(Resource.loading(null))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = mainRepository.getAddress(token)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("phoneResponse", "login: " + response.body().toString())
                        getUserAddressResposne.postValue(Resource.success(response.body() as ShippingAddressResponse))
                        loading.value = false
                    } else {
                        Log.e("phoneResponse", "error: $response")
                        onError("Error : ${response.message()} ")
                        getUserAddressResposne.postValue(Resource.error(response.message(), null))
                    }
                }
            }catch (e:Exception){
                getUserAddressResposne.postValue(Resource.error(e.message.toString(), null))
            }

        }

    }
    fun observeUserAddress(): LiveData<Resource<ShippingAddressResponse>> {
        return getUserAddressResposne
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