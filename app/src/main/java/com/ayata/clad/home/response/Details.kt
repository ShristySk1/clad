package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName

data class Details(
    val advertisements: List<Advertisement>,
    val brands: List<Brand>,
    @SerializedName("just_dropped")
    val justDropped: List<ProductDetail>,
    @SerializedName("most_popular")
    val mostPopular: List<ProductDetail>,
    @SerializedName("popular_this_month")
    val popularThisMonth: List<ProductDetail>,
    val recommended: List<ProductDetail>,
    val sliders: List<Slider>,
    val stories: List<Story>
)