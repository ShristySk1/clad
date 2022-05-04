package com.ayata.clad.profile.giftcard.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Coupon(
    val code: String,
    val description: String,
    @SerializedName("discount_amount")
    val discountAmount: Double,
    @SerializedName("discount_amount_dollar")
    val discountAmountDollar: Double,
    @SerializedName("discount_percent")
    val discountPercent: Double,
    @SerializedName("discount_type")
    val discountType: String,
    val id: Int,
    val title: String,
    @SerializedName("valid_from")
    val validFrom: String,
    @SerializedName("valid_to")
    val validTo: String,
    @SerializedName("validity_count")
    val validityCount: Int
):Serializable