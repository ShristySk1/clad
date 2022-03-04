package com.ayata.clad.shopping_bag.response.checkout


import com.google.gson.annotations.SerializedName

data class Variant(
    @SerializedName("color_hex")
    val colorHex: String?,
    @SerializedName("color_name")
    val colorName: String?,
    @SerializedName("discount_amount")
    val discountAmount: Double?,
    @SerializedName("discount_dollar")
    val discountDollar: Double?,
    @SerializedName("discount_percent")
    val discountPercent: Double?,
    @SerializedName("dollar_price")
    val dollarPrice: Double?,
    @SerializedName("grand_total")
    val grandTotal: Double?,
    val id: Int?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("old_price")
    val oldPrice: Double?,
    val price: Double?,
    val quantity: String?,
    val size: String?,
    @SerializedName("vat_amount")
    val vatAmount: Double?
)