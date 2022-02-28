package com.ayata.clad.wishlist.response.get


import com.google.gson.annotations.SerializedName

data class GetWishListResponse(
    val wishlist: List<Wishlist>
)