package com.ayata.clad.shopping_bag.response.checkout


import com.google.gson.annotations.SerializedName

data class Cart(
    @SerializedName("cart_id")
    val cartId: Int,
    @SerializedName("product_details")
    val productDetails: ProductDetails,
    val selected: Selected
)