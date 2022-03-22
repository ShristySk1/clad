package com.ayata.clad.shopping_bag.model

import java.io.Serializable

data class ModelFinalOrder(
    val payment_token:String="",
    val payment_gateway:String,
    val received_amount:Double,
    val cart_id:List<Int>,
    val address_id:Int,
    val address_type:String//user_address,shipping_address
):Serializable