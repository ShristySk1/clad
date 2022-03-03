package com.ayata.clad.wishlist.response.get


import com.ayata.clad.home.response.ProductDetail
import com.google.gson.annotations.SerializedName

data class Wishlist(
    val id:Int,
    val product: ProductDetail
)