package com.ayata.clad.profile.myorder.order.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Variant(
    val color: String,
    @SerializedName("dollar_price")
    val dollarPrice: Double,
    @SerializedName("npr_price")
    val nprPrice: Double,
    val size: String?,
    @SerializedName("variant_id")
    val variantId: Int
):Serializable