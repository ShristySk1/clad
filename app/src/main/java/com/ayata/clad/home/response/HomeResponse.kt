package com.ayata.clad.home.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HomeResponse (
	@SerializedName("details")
	var details : HomeDetails

): Serializable
