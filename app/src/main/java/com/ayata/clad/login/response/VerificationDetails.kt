package com.ayata.clad.login.response

import com.google.gson.annotations.SerializedName

data class VerificationDetails (

	@SerializedName("id") val id : Int,
	@SerializedName("username") val username : String,
	@SerializedName("mobile_number") val mobile_number : String,
	@SerializedName("token") val token : String
)