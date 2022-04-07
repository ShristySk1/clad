package com.ayata.clad.profile.edit.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Details(
    var dob: String? = null,
    var email: String,
    @SerializedName("first_name")
    var firstName: String,
    @SerializedName("last_name")
    var lastName: String,
    var gender: String? = null,
    var phone_no: String? = null,
) : Serializable