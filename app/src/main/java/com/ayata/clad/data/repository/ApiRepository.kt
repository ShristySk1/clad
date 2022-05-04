package com.ayata.clad.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.home.response.Brand
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.productlist.CategoryPagingDataSource
import com.ayata.clad.profile.edit.response.Details
import com.ayata.clad.search.paging.SearchPagingDataSource
import com.ayata.clad.view_all.paging.BrandPagingDataSource
import com.ayata.clad.view_all.paging.ProductPagingDataSource
import com.google.gson.JsonObject
import okhttp3.MultipartBody

class ApiRepository constructor(private val retrofitService: ApiService) {

    suspend fun phoneAPI(jsonObject: JsonObject) = retrofitService.phoneAPI(jsonObject)
    suspend fun resendOtpAPI(jsonObject: JsonObject) = retrofitService.resendOtpAPI(jsonObject)
    suspend fun otpVerification(jsonObject: JsonObject) =
        retrofitService.otpVerification(jsonObject)

    suspend fun login(token: String) = retrofitService.loginGoogle(token)
    suspend fun dashboardAPI(token: String) = retrofitService.dashboardAPI(token)
    suspend fun categoryListAPI() = retrofitService.categoryListAPI()

    suspend fun wishListApi(token: String) = retrofitService.wishListAPI(token)
    suspend fun addToWishApi(token: String, jsonObject: JsonObject) =
        retrofitService.addToWishAPI(token, jsonObject)

    suspend fun wishlistToCart(token: String, id: Int) =
        retrofitService.wishListToCartAPI(token, id)

    suspend fun removeFromWishAPI(token: String, jsonObject: JsonObject) =
        retrofitService.removeFromWishAPI(token, jsonObject)

    suspend fun cartListApi(token: String) = retrofitService.cartListAPI(token)
    suspend fun addToCartApi(token: String, id: Int) = retrofitService.addToCartAPI(token, id)
    suspend fun removeFromCartAPI(token: String, jsonObject: JsonObject) =
        retrofitService.removeFromCartAPI(token, jsonObject)

    suspend fun minusFromCartAPI(token: String, jsonObject: JsonObject) =
        retrofitService.minusFromCartAPI(token, jsonObject)

    suspend fun cartsSeletApi(token: String, cartId: Int) =
        retrofitService.selectCart(token, cartId)

    suspend fun saveSizeAPI(token: String, jsonObject: JsonObject) =
        retrofitService.saveSizeAPI(token, jsonObject)

    suspend fun saveQuantityAPI(token: String, jsonObject: JsonObject) =
        retrofitService.saveQuantityAPI(token, jsonObject)

    suspend fun profileApi(token: String) = retrofitService.profileAPI(token)
    suspend fun profileUpdateApi(token: String, profile: Details) =
        retrofitService.profileUpdateAPI(token, profile)

    suspend fun orderListApi(token: String) = retrofitService.orderListAPI(token)
    suspend fun orderDetailApi(token: String, id: Int) = retrofitService.orderDetailAPI(token, id)
    suspend fun productListApi(token: String) = retrofitService.productListAPI(token)
    suspend fun productDetailApi(token: String, id: Int) =
        retrofitService.productDetailAPI(token, id)

    suspend fun productAllApi(token: String, page: Int, filter: String) =
        retrofitService.productAllAPI(token, page, filter)

    suspend fun brandListApi(token: String, page: Int, filter: String) =
        retrofitService.productAllAPI(token, page, filter)

    suspend fun logout(token: String) = retrofitService.logout(token)

    //address
    suspend fun addShippingAddress(token: String, json: JsonObject) =
        retrofitService.addShippingAddress(token, json)

    suspend fun getShippingAddress(token: String) = retrofitService.getShippingAddress(token)
    suspend fun addAddress(token: String, json: JsonObject) =
        retrofitService.addUserAddress(token, json)

    suspend fun getAddress(token: String) = retrofitService.getUserAddress(token)

    //paging 3
    fun getViewAllResult(title: String, auth: String): LiveData<PagingData<ProductDetail>> {
        Log.d("calledheretwo", "getViewAllResult: ");

        return Pager(
            config = PagingConfig(
                pageSize = 4,
                maxSize = 100,
                initialLoadSize = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ProductPagingDataSource(auth, title, retrofitService) }
        ).liveData
    }

    //brand
    fun getViewAllResultBrand(title: String, auth: String): LiveData<PagingData<Brand>> {
        Log.d("calledheretwo", "getViewAllResult: ");
        return Pager(
            config = PagingConfig(
                pageSize = 4,
                maxSize = 100,
                initialLoadSize = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { BrandPagingDataSource(auth, title, retrofitService) }
        ).liveData
    }

    //checkout order
    suspend fun checkoutOrder(token: String, json: JsonObject) =
        retrofitService.checkoutOrder(token, json)

    //payment gateways
    suspend fun getPaymentGateways(token: String) = retrofitService.getPaymentGateways(token)

    //order
    suspend fun getOrder(token: String) = retrofitService.getOrder(token)

    //cancel order
    suspend fun cancelOrder(token: String, orderId: Int, comment: String, reason: String) =
        retrofitService.cancelOrder(token, orderId, comment, reason)
    //return order
    suspend fun returnOrder(
        token: String,
        body: JsonObject
    ) = retrofitService.returnOrder(token, body)
    //search
    suspend fun searchOrder(token: String, query: String, page: Int) =
        retrofitService.searchProduct(token, query, page)

    fun getSearchResult(title: String, auth: String): LiveData<PagingData<ProductDetail>> {
        Log.d("tet5stcall", "getViewAllResult: ");
        return Pager(
            config = PagingConfig(
                pageSize = 4,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchPagingDataSource(auth, title, retrofitService) }
        ).liveData
    }

    //product list from category
    fun searchProductListFromCategory(
        token: String,
        map: Map<String, String>
    ): LiveData<PagingData<ProductDetail>> {
        Log.d("tet5stcall", "getViewAllResult: ");
        return Pager(
            config = PagingConfig(
                pageSize = 4,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CategoryPagingDataSource(
                    token,
                    map,
                    retrofitService
                )
            }
        ).liveData
    }

    //get coupon
    suspend fun getCoupons(token: String) = retrofitService.coupons(token)

    //apply coupon
    suspend fun applyCoupons(token: String, coupon_code: String) =
        retrofitService.applyCoupon(token, coupon_code)

    //review api
    suspend fun getReviewApi(token: String) = retrofitService.getReview(token)
    suspend fun postReviewApi(
        token: String,
        body: JsonObject
    ) = retrofitService.postReview(token, body)

    //image post review
    suspend fun uploadImageReviewApi(photo: List<MultipartBody.Part>) =
        retrofitService.uploadPhoto(photo)

    //delete photo
    suspend fun deleteImageReviewApi(photo: Int) = retrofitService.deletePhoto(photo)

    //brand detail
    suspend fun getBrandDetail(slug: String) = retrofitService.getBrandDetail(slug)

    //order filter
    suspend fun getOrderFilter(
        token: String,
        page: Int,
        size: String,
        color: String,
        brand: String
    ) = retrofitService.getOrderFilter(token, page, size, color, brand)

    //faq
    suspend fun getFAQ(slug: String) = retrofitService.getFAQ(slug)
    suspend fun getProduct(token:String,productId: Int) = retrofitService.getProduct(token,productId)
}