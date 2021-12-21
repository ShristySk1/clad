package com.ayata.clad.profile

import java.io.Serializable

data class ModelOrder(
    val date: String,
    val itemId: String,
    val name: String,
    val quantity: String,
    val image: String
) : Serializable
sealed class MyOrderRecyclerViewItem {
    class Title(
        val date: String,
        val itemId: String,
    ) : MyOrderRecyclerViewItem()

    class Product(
        val name: String,
        val quantity: String,
        val image: String

    ) : MyOrderRecyclerViewItem()
    class Divider(
    ) : MyOrderRecyclerViewItem()
}