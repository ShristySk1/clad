package com.ayata.clad.shopping_bag.response.checkout


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Selected(
    @SerializedName("color_hex")
    val colorHex: String,
    @SerializedName("color_name")
    val colorName: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val quantity: Int,
    val size: String,
    val sku: Int,
    @SerializedName("v_dollar_total")
    val vDollarTotal: Double,
    @SerializedName("v_total")
    val vTotal: Double,
    @SerializedName("variant_id")
    val variantId: Int
):Serializable