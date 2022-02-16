package com.ayata.clad.home.response

import com.google.gson.annotations.SerializedName

data class HomeResponse (
	@SerializedName("details")
	var details : HomeDetails

)
