package com.ayata.clad.data.repository

import androidx.annotation.IdRes
import com.ayata.clad.data.network.ApiService
import com.google.gson.JsonObject

class ApiRepository constructor(private val retrofitService: ApiService) {

    suspend fun phoneAPI(jsonObject: JsonObject) = retrofitService.phoneAPI(jsonObject)
    suspend fun resendOtpAPI(jsonObject: JsonObject) = retrofitService.resendOtpAPI(jsonObject)
    suspend fun otpVerification(jsonObject: JsonObject) = retrofitService.otpVerification(jsonObject)
    suspend fun dashboardAPI()=retrofitService.dashboardAPI()
    suspend fun categoryListAPI()=retrofitService.categoryListAPI()
    suspend fun wishListApi(token:String)=retrofitService.wishListAPI(token)
    suspend fun addToWishApi(token:String,jsonObject: JsonObject)=retrofitService.addToWishAPI(token,jsonObject)
    suspend fun removeFromWishAPI(token:String,jsonObject: JsonObject)=retrofitService.removeFromWishAPI(token,jsonObject)
    suspend fun cartListApi(token:String)=retrofitService.cartListAPI(token)
    suspend fun addToCartApi(token:String,jsonObject: JsonObject)=retrofitService.addToCartAPI(token,jsonObject)
    suspend fun removeFromCartAPI(token:String,jsonObject: JsonObject)=retrofitService.removeFromCartAPI(token,jsonObject)
    suspend fun saveSizeAPI(token:String,jsonObject: JsonObject)=retrofitService.saveSizeAPI(token,jsonObject)
    suspend fun saveQuantityAPI(token:String,jsonObject: JsonObject)=retrofitService.saveQuantityAPI(token,jsonObject)
    suspend fun profileApi(token:String)=retrofitService.profileAPI(token)
    suspend fun orderListApi(token:String)=retrofitService.orderListAPI(token)
    suspend fun orderDetailApi(token:String,id:Int)=retrofitService.orderDetailAPI(token,id)
    suspend fun productListApi(token:String)=retrofitService.productListAPI(token)
    suspend fun productDetailApi(token:String,id:Int)=retrofitService.productDetailAPI(token,id)
    suspend fun productAllApi(token:String,offset:Int,filter:String)=retrofitService.productAllAPI(token,offset,filter)
    suspend fun brandListApi(token:String,offset:Int)=retrofitService.brandListAPI(token,offset)


}