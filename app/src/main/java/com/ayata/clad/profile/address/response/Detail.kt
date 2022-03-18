package com.ayata.clad.profile.address.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Detail(
    val id:Int,
    val city: String,
    @SerializedName("contact_number")
    val contactNumber: String,
    val state: Int,
    @SerializedName("street_name")
    val streetName: String,
    val title: String,
    @SerializedName("zip_code")
    val zipCode: String
) : Serializable