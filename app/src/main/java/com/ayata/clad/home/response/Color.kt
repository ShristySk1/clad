package com.ayata.clad.home.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Color (

	@SerializedName("id") val id : Int?,
	@SerializedName("hex_value") val hex_value : String="",
	@SerializedName("name") val name : String?
): Serializable