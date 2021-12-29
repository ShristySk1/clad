package com.ayata.clad.data.repository

import com.ayata.clad.data.network.ApiService
import com.google.gson.JsonObject

class ApiRepository constructor(private val retrofitService: ApiService) {

    suspend fun phoneAPI(jsonObject: JsonObject) = retrofitService.phoneAPI(jsonObject)
    suspend fun otpVerification(jsonObject: JsonObject) = retrofitService.otpVerification(jsonObject)
    suspend fun dashboardAPI()=retrofitService.dashboardAPI()


}