package com.ayata.clad.home.response

import com.google.gson.annotations.SerializedName



data class Sliders (

	@SerializedName("id") val id : Int,
	@SerializedName("title") val title : String,
	@SerializedName("image_url") val image_url : String
)