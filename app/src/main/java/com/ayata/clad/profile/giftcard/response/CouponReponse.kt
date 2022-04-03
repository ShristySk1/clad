package com.ayata.clad.profile.giftcard.response


import com.google.gson.annotations.SerializedName

data class CouponReponse(
    val message:String?=null,
    val coupons: List<Coupon>?=null
)