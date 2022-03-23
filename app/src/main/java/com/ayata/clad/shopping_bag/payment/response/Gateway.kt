package com.ayata.clad.shopping_bag.payment.response


import com.google.gson.annotations.SerializedName

data class Gateway(
    @SerializedName("icon_url")
    val iconUrl: String,
    val id: Int,
    @SerializedName("is_active")
    val isActive: Boolean,
    val name: String,
    @SerializedName("refund_charge")
    val refundCharge: Int,
    @SerializedName("transaction_charge")
    val transactionCharge: Int
)