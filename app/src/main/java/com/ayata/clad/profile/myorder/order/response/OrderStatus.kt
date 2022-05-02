package com.ayata.clad.profile.myorder.order.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderStatus(
    val date: String?="",
    val status: String,
    val is_active:Boolean?=false
):Serializable