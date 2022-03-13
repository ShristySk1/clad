package com.ayata.clad.data.network

import android.content.Context
import com.ayata.clad.data.network.interceptor.NetworkConnectionInterceptor
import com.ayata.clad.profile.edit.response.Details
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


interface ApiService {

    companion object {

        private const val baseUrl = "https://clad.ayata.com.np/api/v1/"
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
                    OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
//                        .addInterceptor(NetworkConnectionInterceptor(WeakReference(context)))
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
    suspend fun loginGoogle(@Field("auth_token")auth_token: String):Response<JsonObject>
    //dashboard
    @GET("home/")
    suspend fun dashboardAPI(@Header("Authorization") token: String): Response<JsonObject>

    //category
    @GET("category-list/")
    suspend fun categoryListAPI(): Response<JsonObject>

    @GET("category-products/")
    suspend fun categoryProductListAPI(
        @Query("category_id") categoryId: Int,
        @Query("page") page: Int
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
        @Field("wishlist_id") id:Int
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
        @Field("variant_id")variant_id: Int
    ): Response<JsonObject>

    @POST("remove-from-cart/")
    suspend fun removeFromCartAPI(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Response<JsonObject>
    @POST("decrease-cart/")
    suspend fun minusFromCartAPI(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Response<JsonObject>

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

    //view all-- pagination
//    @GET("brand-list/")
//    suspend fun brandListAPI( @Header("Authorization") token: String,@Query("offset")offset:Int): Response<JsonObject>

    @GET("products/all/")
    suspend fun productAllAPI(
        @Header("Authorization") token: String,
        @Query("page") offset: Int,
        @Query("title") filter: String
    ): Response<JsonObject>

    //logout
    @POST("account/logout/")
    suspend fun logout(@Header("Authorization") token: String): Response<JsonObject>
}