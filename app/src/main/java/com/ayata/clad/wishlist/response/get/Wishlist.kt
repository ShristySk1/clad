package com.ayata.clad.wishlist.response.get


import com.ayata.clad.home.response.ProductDetail
import com.google.gson.annotations.SerializedName

data class Wishlist(
    val wishlist_id:Int,
    val product: ProductDetail
)