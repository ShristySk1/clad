package com.ayata.clad.shopping_bag.response.checkout


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CartResponse(
    @SerializedName("message")
    val message:String?=null,
    @SerializedName("cart")
    val cart: List<Cart>,
    @SerializedName("cart_subtotal_dollar")
    val cartTotalDollar: Double,
    @SerializedName("cart_subtotal_npr")
    val cartTotalNpr: Double,

   //grand total
    @SerializedName("cart_grand_total_dollar")
    val cartGrandTotalDollar: Double,
    @SerializedName("cart_grand_total_npr")
    val cartGrandTotalNpr: Double,

    //shipping price
    @SerializedName("shipping_cost_npr")
    val cartShippingPriceNpr: Double,
    @SerializedName("shipping_cost_dollar")
    val cartShippingPriceDollar: Double,

    //promo code discount price
    @SerializedName("promo_discount_npr")
    val cartPromoDiscountNpr: Double,
    @SerializedName("promo_discount_dollar")
    val cartPromoDiscountDollar: Double,
    //coupon_code
    @SerializedName("coupon_code")
    val coupon_code:String?,
):Serializable