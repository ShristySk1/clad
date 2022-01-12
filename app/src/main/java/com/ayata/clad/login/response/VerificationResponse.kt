package com.ayata.clad.login.response

import com.google.gson.annotations.SerializedName


data class VerificationResponse (

	@SerializedName("message") val message : String,
	@SerializedName("details") val details : VerificationDetails
)