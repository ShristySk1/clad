package com.ayata.clad.data.network

import android.content.Context
import com.ayata.clad.BuildConfig
import com.ayata.clad.data.network.interceptor.NetworkConnectionInterceptor
import com.ayata.clad.product.qa.response.ResponseQA
import com.ayata.clad.profile.address.response.ShippingAddressResponse
import com.ayata.clad.profile.edit.response.Details
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface ApiService {

    companion object {
        private const val baseUrl = BuildConfig.BASE_URL
        private val httpLoggingInterceptor = run {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
        }

        var retrofitService: ApiService? = null
        fun getInstance(context: Context): ApiService {
            if (retrofitService == null) {
                val client: OkHttpClient =
                    OkHttpClient.Builder()
                        .addInterceptor(httpLoggingInterceptor)
                        .addInterceptor(NetworkConnectionInterceptor(context))
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS).build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(ApiService::class.java)
            }

            return retrofitService!!
        }

    }

    //login-register-verification
    @POST("register/")
    suspend fun phoneAPI(@Body jsonObject: JsonObject): Response<JsonObject>

    @POST("resend-otp/")
    suspend fun resendOtpAPI(@Body jsonObject: JsonObject): Response<JsonObject>

    @POST("otp-verify/")
    suspend fun otpVerification(@Body jsonObject: JsonObject): Response<JsonObject>

    //google login
    @POST("oauth/google/")
    @FormUrlEncoded
    suspend fun loginGoogle(
        @Field("auth_token") auth_token: String,
        @Field("refer_code") referCode: String
    ): Response<JsonObject>

    //dashboard
    @GET("home/")
    suspend fun dashboardAPI(@Header("Authorization") token: String): Response<JsonObject>

    //category
    @GET("category-list/")
    suspend fun categoryListAPI(): Response<JsonObject>

    @GET("products/all/")
    suspend fun categoryProductListAPI(
        @Header("Authorization") token: String,
        @QueryMap(encoded = true) map: Map<String, String>,
        @Query("page") page: Int,
    ): Response<JsonObject>

    //user wishlist
    @GET("wishlist/")
    suspend fun wishListAPI(@Header("Authorization") token: String): Response<JsonObject>

    @POST("add-to-wishlist/")
    suspend fun addToWishAPI(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Response<JsonObject>

    @POST("wishlist-to-cart/")
    @FormUrlEncoded
    suspend fun wishListToCartAPI(
        @Header("Authorization") token: String,
        @Field("wishlist_id") id: Int
    ): Response<JsonObject>

    @POST("remove-wishlist-item/")
    suspend fun removeFromWishAPI(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Response<JsonObject>


    //user cart info
    @GET("cart-list/")
    suspend fun cartListAPI(@Header("Authorization") token: String): Response<JsonObject>

    @POST("add-to-cart/")
    @FormUrlEncoded
    suspend fun addToCartAPI(
        @Header("Authorization") token: String,
        @Field("variant_id") variant_id: Int
    ): Response<JsonObject>

    @POST("remove-cart-item/")
    suspend fun removeFromCartAPI(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Response<JsonObject>

    @POST("decrease-cart/")
    suspend fun minusFromCartAPI(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Response<JsonObject>

    //cart select
    @POST("select-cart/")
    @FormUrlEncoded
    suspend fun selectCart(
        @Header("Authorization") token: String,
        @Field("cart_id") cartId: Int
    ): Response<JsonObject>

    @POST("remove-coupon/")
    suspend fun deleteCoupon(@Header("Authorization") token: String): Response<JsonObject>


    //not made
    @POST("size/")
    suspend fun saveSizeAPI(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Response<JsonObject>

    //not made
    @POST("quantity/")
    suspend fun saveQuantityAPI(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Response<JsonObject>


    //user profile
    @GET("account/profile/")
    suspend fun profileAPI(@Header("Authorization") token: String): Response<JsonObject>

    @POST("account/profile/")
    suspend fun profileUpdateAPI(
        @Header("Authorization") token: String,
        @Body profile: Details?
    ): Response<JsonObject>

    @GET("user-orders/")
    suspend fun orderListAPI(@Header("Authorization") token: String): Response<JsonObject>

    @GET("user-order-details/")
    suspend fun orderDetailAPI(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): Response<JsonObject>

    //product list
    @GET("products-list/")
    suspend fun productListAPI(@Header("Authorization") token: String): Response<JsonObject>

    @GET("product/details/")
    suspend fun productDetailAPI(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): Response<JsonObject>

    //view all pagination
    @GET("products/all/")
    suspend fun productAllAPI(
        @Header("Authorization") token: String,
        @Query("page") offset: Int,
        @Query("title") filter: String
    ): Response<JsonObject>

    //logout
    @POST("account/logout/")
    suspend fun logout(@Header("Authorization") token: String): Response<JsonObject>

    //address shipping
    @GET("account/user/shipping-address/")
    suspend fun getShippingAddress(@Header("Authorization") token: String): Response<ShippingAddressResponse>

    @POST("account/user/shipping-address/")
    suspend fun addShippingAddress(
        @Header("Authorization") token: String,
        @Body json: JsonObject
    ): Response<JsonObject>

    //address own
    @GET("account/user/address/")
    suspend fun getUserAddress(@Header("Authorization") token: String): Response<ShippingAddressResponse>

    @POST("account/user/address/")
    suspend fun addUserAddress(
        @Header("Authorization") token: String,
        @Body json: JsonObject
    ): Response<JsonObject>

    //checkout order
    @POST("checkout/")
    suspend fun checkoutOrder(
        @Header("Authorization") token: String,
        @Body json: JsonObject
    ): Response<JsonObject>

    //search
    @GET("products/all/")
    suspend fun searchProduct(
        @Header("Authorization") token: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Response<JsonObject>

    //payment gateway
    @GET("payment-gateways/")
    suspend fun getPaymentGateways(
        @Header("Authorization") token: String,
    ): Response<JsonObject>

    //order detail
    @GET("user-orders/")
    suspend fun getOrder(
        @Header("Authorization") token: String,
    ): Response<JsonObject>

    //cancel order
    @POST("cancel-order/")
    @FormUrlEncoded
    suspend fun cancelOrder(
        @Header("Authorization") token: String,
        @Field("order_id") orderId: Int,
        @Field("comment") comment: String,
        @Field("reason") reason: String
    ): Response<JsonObject>

    //return order
    @POST("return-order/")
    suspend fun returnOrder(
        @Header("Authorization") token: String,
        @Body json: JsonObject
    ): Response<JsonObject>

    //coupons
    @GET("coupons/")
    suspend fun coupons(
        @Header("Authorization") token: String
    ): Response<JsonObject>

    //apply coupons
    @POST("apply-coupon/")
    @FormUrlEncoded
    suspend fun applyCoupon(
        @Header("Authorization") token: String,
        @Field("coupon_code") code: String
    ): Response<JsonObject>

    //review
    @GET("reviews/")
    suspend fun getReview(
        @Header("Authorization") token: String
    ): Response<JsonObject>

    //post review
    @POST("reviews/")
    suspend fun postReview(
        @Header("Authorization") token: String,
        @Body json: JsonObject
    ): Response<JsonObject>

    @Multipart
    @POST("upload-photo/")
    suspend fun uploadPhoto(
        @Part images: List<MultipartBody.Part>,
    ): Response<JsonObject>

    @POST("delete-photo/")
    @FormUrlEncoded
    suspend fun deletePhoto(
        @Field("image_id") image_id: Int,
    ): Response<JsonObject>

    //brand detail
    @GET("brand/details")
    suspend fun getBrandDetail(
        @Query("slug") slug: String
    ): Response<JsonObject>

    //order filter
    @GET("products/all/")
    suspend fun getOrderFilter(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: String,
        @Query("color") colorHex: String,
        @Query("brand") brand: String
    ): Response<JsonObject>

    //faq
    @GET("questions/")
    suspend fun getFAQ(
        @Header("Authorization") token: String,
        @Query("product_id") productId: Int
    ): Response<ResponseQA>

    @POST("questions/")
    suspend fun addFAQQuestion(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Response<ResponseQA>

    @POST("delete-question/")
    @FormUrlEncoded
    suspend fun deleteFAQQuestion(
        @Header("Authorization") token: String,
        @Field("q_id") questionId: Int
    ): Response<ResponseQA>

    @GET("product/details/")
    suspend fun getProduct(
        @Header("Authorization") token: String,
        @Query("product_id") productId: Int
    ): Response<JsonObject>

}