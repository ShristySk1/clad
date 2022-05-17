package com.ayata.clad.productlist.response


import com.google.gson.annotations.SerializedName

data class Sizes(
    @SerializedName("Height")
    val height: List<Height>,
    @SerializedName("Others")
    val others: List<MySize>,
    @SerializedName("US")
    val uS: List<US>
)