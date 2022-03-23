package com.ayata.clad.shopping_bag.payment.response


import com.ayata.clad.shopping_bag.model.ModelPaymentMethod
import com.google.gson.annotations.SerializedName

data class PaymentResponse(
    val gateways: List<ModelPaymentMethod>
)