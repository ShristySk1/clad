package com.ayata.clad.wishlist.response.add


data class AddWishListResponse(
    val details: List<Detail>,
    val message: String
)