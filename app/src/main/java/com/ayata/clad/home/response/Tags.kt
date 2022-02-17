package com.ayata.clad.home.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Tags (

	@SerializedName("id") val id : Int,
	@SerializedName("title") val title : String,
	@SerializedName("description") val description : String
): Serializable