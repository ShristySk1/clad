package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Variant(
    @SerializedName("color_hex")
    val colorHex: String,
    @SerializedName("color_name")
    val colorName: String,
    @SerializedName("discount_amount")
    val discountAmount: Double,
    @SerializedName("discount_dollar")
    val discountDollar: Double,
    @SerializedName("discount_percent")
    val discountPercent: Double,
    @SerializedName("dollar_price")
    val dollarPrice: Double,
    @SerializedName("grand_total")
    val grandTotal: Double,
    @SerializedName("image_url")
    val imageUrl: List<String>,
    @SerializedName("old_price")
    val oldPrice: Double,
    val price: Double,
    val quantity: Int,
    val size: String,
    @SerializedName("variant_id")
    val variantId: Int,
    @SerializedName("vat_amount")
    val vatAmount: Double,
    @SerializedName("is_in_cart")
    var isInCart: Boolean,
    @SerializedName("is_in_wishlist")
    var isInWishlist: Boolean,
    @SerializedName("stock_status")
    var stockStatus:String

):Serializable