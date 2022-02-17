package com.ayata.clad.home.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Brands (

	@SerializedName("id") val id : Int,
	@SerializedName("name") val name : String,
	@SerializedName("icon_url") val icon_url : String
): Serializable