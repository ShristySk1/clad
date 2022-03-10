package com.ayata.clad.shopping_bag.response.checkout


import com.google.gson.annotations.SerializedName

data class ProductDetails(
    val brand: Brand,
    val categories: List<Category>,
    val coupon: Any,
    val description: String,
    @SerializedName("discount_percent")
    val discountPercent: Double,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("is_coming_soon")
    val isComingSoon: Boolean,
    val isCouponAvailable: Boolean,
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
    val priority: Int,
    @SerializedName("product_address")
    val productAddress: String,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("product_weight")
    val productWeight: Any,
    @SerializedName("reference_code")
    val referenceCode: Any,
    val reviews: Reviews,
    @SerializedName("seo_description")
    val seoDescription: String,
    @SerializedName("seo_keywords")
    val seoKeywords: List<Any>,
    @SerializedName("seo_title")
    val seoTitle: Any,
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
    val visibility: Boolean
)