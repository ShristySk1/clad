package com.ayata.clad.data.repository

import com.ayata.clad.data.network.ApiService
import com.google.gson.JsonObject

class ApiRepository constructor(private val retrofitService: ApiService) {

    suspend fun phoneAPI(phone:String) = retrofitService.phoneAPI(phone)
    suspend fun otpVerification(phone: String,otp:String) = retrofitService.otpVerification(phone,otp)


}