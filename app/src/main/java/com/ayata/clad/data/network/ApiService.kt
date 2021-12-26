package com.ayata.clad.data.network

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface ApiService {

    companion object {
        private val httpLoggingInterceptor = run {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
            }
        }
        val client: OkHttpClient =
            OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS).build()
        var retrofitService: ApiService? = null
        fun getInstance(): ApiService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://backend.medipuzzle.avyaas.com/api/v1/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(ApiService::class.java)
            }

            return retrofitService!!
        }

    }

    @GET("auth/google")
    suspend fun phoneAPI(@Query("phone") phone: String): Response<JsonObject>

    @GET("otp")
    suspend fun otpVerification(@Query("phone") phone: String,@Query("otp") otp: String): Response<JsonObject>

//    @Headers("Content-Type: application/json")
//    @POST("hangingman/bulk/")
//    suspend fun hangmanScoreSave(
//        @Header("Authorization") token: String,
//        @Body jsonData: HangmanScoreList
//    ): Response<JsonObject>
//
//    @POST("mockviva/bulk/")
//    suspend fun mockVivaScoreSave(
//        @Header("Authorization") token: String,
//        @Body jsonData: MockVivaScoreList
//    ): Response<JsonObject>

//    @GET("hangingman/leaderboard")
//    suspend fun getLeaderBoard(@Header("Authorization") token: String): Response<LeaderBoardResponse>

}