package com.ayata.clad.data.repository

import com.ayata.clad.data.network.ApiService
import com.ayata.clad.profile.edit.response.Details
import com.google.gson.JsonObject

class ApiRepository constructor(private val retrofitService: ApiService) {

    suspend fun phoneAPI(jsonObject: JsonObject) = retrofitService.phoneAPI(jsonObject)
    suspend fun resendOtpAPI(jsonObject: JsonObject) = retrofitService.resendOtpAPI(jsonObject)
    suspend fun otpVerification(jsonObject: JsonObject) = retrofitService.otpVerification(jsonObject)
    suspend fun login(token: String) = retrofitService.loginGoogle(token)
    suspend fun dashboardAPI(token: String)=retrofitService.dashboardAPI(token)
    suspend fun categoryListAPI()=retrofitService.categoryListAPI()
    suspend fun categoryProductListAPI(categoryId: Int,page:Int) =retrofitService.categoryProductListAPI(categoryId,page)
    suspend fun wishListApi(token:String)=retrofitService.wishListAPI(token)
    suspend fun addToWishApi(token:String,jsonObject: JsonObject)=retrofitService.addToWishAPI(token,jsonObject)
    suspend fun wishlistToCart(token:String,id: Int)=retrofitService.wishListToCartAPI(token,id)
    suspend fun removeFromWishAPI(token:String,jsonObject: JsonObject)=retrofitService.removeFromWishAPI(token,jsonObject)
    suspend fun cartListApi(token:String)=retrofitService.cartListAPI(token)
    suspend fun addToCartApi(token:String,id: Int)=retrofitService.addToCartAPI(token,id)
    suspend fun removeFromCartAPI(token:String,jsonObject: JsonObject)=retrofitService.removeFromCartAPI(token,jsonObject)
    suspend fun minusFromCartAPI(token:String,jsonObject: JsonObject)=retrofitService.minusFromCartAPI(token,jsonObject)
    suspend fun cartsSeletApi(token:String,cartId:Int)=retrofitService.selectCart(token,cartId)
    suspend fun saveSizeAPI(token:String,jsonObject: JsonObject)=retrofitService.saveSizeAPI(token,jsonObject)
    suspend fun saveQuantityAPI(token:String,jsonObject: JsonObject)=retrofitService.saveQuantityAPI(token,jsonObject)
    suspend fun profileApi(token:String)=retrofitService.profileAPI(token)
    suspend fun profileUpdateApi(token:String,profile:Details)=retrofitService.profileUpdateAPI(token,profile)
    suspend fun orderListApi(token:String)=retrofitService.orderListAPI(token)
    suspend fun orderDetailApi(token:String,id:Int)=retrofitService.orderDetailAPI(token,id)
    suspend fun productListApi(token:String)=retrofitService.productListAPI(token)
    suspend fun productDetailApi(token:String,id:Int)=retrofitService.productDetailAPI(token,id)
    suspend fun productAllApi(token:String,page:Int,filter:String)=retrofitService.productAllAPI(token,page,filter)
    suspend fun brandListApi(token:String,page:Int,filter:String)=retrofitService.productAllAPI(token, page,filter)
    suspend fun logout(token: String)=retrofitService.logout(token)
    //address
    suspend fun addAddress(token: String,json:JsonObject)=retrofitService.addShippingAddress(token,json)
    suspend fun getAddress(token: String)=retrofitService.getShippingAddress(token)


}