package com.ayata.clad.profile.address.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Detail(
    val id:Int,
    val city: String,
    @SerializedName("phone_no")
    val contactNumber: String,
    val state: String,
    @SerializedName("street_name")
    val streetName: String,
    val title: String,
    @SerializedName("postal_code")
    val zipCode: String
) : Serializable