package com.ayata.clad.home.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Just_dropped (

	@SerializedName("id") val id : Int,
	@SerializedName("name") val name : String,
	@SerializedName("slug") val slug : String,
	@SerializedName("categories") val categories : List<Categories>,
	@SerializedName("price") val price : Int,
	@SerializedName("old_price") val old_price : Int,
	@SerializedName("variants") val variants : List<Variants>,
	@SerializedName("image_url") val image_url : String,
	@SerializedName("product_weight") val product_weight : String,
	@SerializedName("description") val description : String,
	@SerializedName("summary") val summary : String,
	@SerializedName("tags") val tags : List<Tags>,
	@SerializedName("unit") val unit : Int,
	@SerializedName("visibility") val visibility : Boolean,
	@SerializedName("reference_code") val reference_code : String,
	@SerializedName("barcode") val barcode : String,
	@SerializedName("brand") val brand : Brand,
	@SerializedName("owner") val owner : String,
	@SerializedName("store") val store : String,
	@SerializedName("product_address") val product_address : String,
	@SerializedName("seo_title") val seo_title : String,
	@SerializedName("seo_description") val seo_description : String,
	@SerializedName("seo_keywords") val seo_seo_keywordswords : List<String>,
	@SerializedName("vat_included") val vat_included : Boolean,
	@SerializedName("vat") val vat : Int,
	@SerializedName("is_new") val is_new : Boolean,
	@SerializedName("is_on_sale") val is_on_sale : Boolean,
	@SerializedName("is_coming_soon") val is_coming_soon : Boolean,
	@SerializedName("order_count") val order_count : Int,
	@SerializedName("likes") val likes : Int,
	@SerializedName("priority") val priority : Int,
	@SerializedName("isCouponAvailable") val isCouponAvailable : Boolean,
	@SerializedName("coupon") val coupon : String,
	@SerializedName("is_in_wishlist") val is_in_wishlist : Boolean,
	@SerializedName("reviews") val reviews : Reviews
): Serializable