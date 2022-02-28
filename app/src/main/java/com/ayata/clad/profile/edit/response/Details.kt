package com.ayata.clad.profile.edit.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Details(
    var dob: String,
    var email: String,
    @SerializedName("full_name")
    var fullName: String,
    var gender: String
) : Serializable