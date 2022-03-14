package com.ayata.clad.wishlist.response.get

import com.ayata.clad.home.response.ProductDetail


data class Wishlist(
    val wishlist_id:Int,
    val product: ProductDetail
)