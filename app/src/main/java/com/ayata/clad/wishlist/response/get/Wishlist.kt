package com.ayata.clad.wishlist.response.get

import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.shopping_bag.response.checkout.Selected
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Wishlist(
    val wishlist_id:Int,
    val selected:Selected,
    @SerializedName("product_details")
    val product: ProductDetail
):Serializable