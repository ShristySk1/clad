package com.ayata.clad.home.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Variants (

	@SerializedName("id") val id : Int,
	@SerializedName("quantity") val quantity : Int,
	@SerializedName("old_price") val old_price : Int,
	@SerializedName("price") val price : Int,
	@SerializedName("dollar_price") val dollar_price : Int,
	@SerializedName("discount_dollar") val discount_dollar : Int,
	@SerializedName("discount_percent") val discount_percent : Int,
	@SerializedName("discount_amount") val discount_amount : Int,
	@SerializedName("vat_amount") val vat_amount : Int,
	@SerializedName("grand_total") val grand_total : Int,
	@SerializedName("color") val color : Color,
	@SerializedName("image_url") val image_url : String,
	@SerializedName("size") val size : String
): Serializable