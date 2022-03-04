package com.ayata.clad.shopping_bag.response.checkout


import com.google.gson.annotations.SerializedName

data class Cart(
    val id: Int?,
    @SerializedName("is_cancelled")
    val isCancelled: Boolean?,
    @SerializedName("is_completed")
    val isCompleted: Boolean?,
    @SerializedName("is_ordered")
    val isOrdered: Boolean?,
    @SerializedName("is_taken")
    val isTaken: Boolean?,
    val product: Product?
)