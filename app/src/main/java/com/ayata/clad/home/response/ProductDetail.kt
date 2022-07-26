package com.ayata.clad.home.response


import com.ayata.clad.profile.giftcard.response.Coupon
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.temporal.TemporalAmount

data class ProductDetail(
    val brand: Brand?,
//    val category: List<Category>,
    val coupon: Coupon?=null,
    val description: String="",
    @SerializedName("discount_percent")
    val discountPercent: Double=0.0,
    @SerializedName("discount_amount")
    val discountAmount: Double=0.0,
    @SerializedName("discount_type")
    val discountType: String="",
    @SerializedName("image_url")
    val imageUrl: List<String>,
    @SerializedName("is_coming_soon")
    val isComingSoon: Boolean,
    val isCouponAvailable: Boolean,
    @SerializedName("is_in_cart")
    var isInCart: Boolean,
    @SerializedName("is_in_wishlist")
    var isInWishlist: Boolean,
    @SerializedName("is_new")
    val isNew: Boolean,
    @SerializedName("is_on_sale")
    val isOnSale: Boolean,
    val likes: Int,
    val name: String,
    @SerializedName("old_price")
    val oldPrice: Double,
    @SerializedName("order_count")
    val orderCount: Int,
    val price: Double,
    val dollar_price: Double=0.0,
    val old_dollar_price: Double=0.0,
    val priority: Int,
    @SerializedName("product_address")
    val productAddress: String,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("product_weight")
    val productWeight: String,
    @SerializedName("reference_code")
    val referenceCode: String,
    val reviews: Reviews?,
    @SerializedName("seo_description")
    val seoDescription: String,
    @SerializedName("seo_keywords")
    val seoKeywords: List<String>,
    @SerializedName("queries")
    val queries: List<Query>?,
    @SerializedName("seo_title")
    val seoTitle: String,
    val slug: String,
    val store: String,
    val summary: String,
    val tags: List<Tag>,
    val unit: String,
    val variants: List<Variant>,
    val vat: Double,
    @SerializedName("vat_included")
    val vatIncluded: Boolean,
    val vendor: String,
    val visibility: Boolean,
) : Serializable{

}