package com.ayata.clad.home.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class HomeDetails (

	@SerializedName("sliders") val sliders : List<Sliders>,
	@SerializedName("popular_this_month") val popular_this_month : List<ProductDetail>,
	@SerializedName("recommended") val recommended : List<ProductDetail>,
	@SerializedName("brands") val brands : List<Brand>,
	@SerializedName("just_dropped") val just_dropped : List<ProductDetail>,
	@SerializedName("most_popular") val most_popular : List<ProductDetail>
): Serializable