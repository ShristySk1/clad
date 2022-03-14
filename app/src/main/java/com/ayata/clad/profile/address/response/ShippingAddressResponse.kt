package com.ayata.clad.profile.address.response


import com.google.gson.annotations.SerializedName

data class ShippingAddressResponse(
    val details: List<Detail>
)