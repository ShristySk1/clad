package com.ayata.clad.shopping_bag.model

import com.google.gson.annotations.SerializedName

data class ModelPaymentMethod(
    val id:Int,
    val name: String,
    @SerializedName("icon_url")
    val logo: String,
    var isSelected: Boolean=false,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("refund_charge")
    val refundCharge: Int,
    @SerializedName("transaction_charge")
    val transactionCharge: Int
)

