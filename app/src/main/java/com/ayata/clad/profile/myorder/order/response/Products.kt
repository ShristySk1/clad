package com.ayata.clad.profile.myorder.order.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Products(
    @SerializedName("image_url")
    val imageUrl: String,
    val name: String,
    @SerializedName("product_id")
    val productId: Int,
    val quantity: Int,
    val variant: Variant
):Serializable