package com.ayata.clad.shopping_bag.model

import java.io.Serializable

data class ModelFinalOrder(
    val payment_gateway:String,
    val received_amount:Double,
    val cart_id:List<Int>
):Serializable