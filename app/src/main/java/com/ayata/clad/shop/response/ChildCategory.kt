package com.ayata.clad.shop.response

import java.io.Serializable


data class ChildCategory(
    val id: Int,
    val image: String,
    val title: String,
    val comment: String = "",
) : Serializable