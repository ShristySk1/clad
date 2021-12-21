package com.ayata.clad.filter

import java.io.Serializable

data class ModelFilter(
    val title: String,
    val subTitle: String,
    val colorList: List<Int>
) : Serializable

sealed class MyFilterRecyclerViewItem {
    class Title(
        val id:Int,
        val title: String,
        val subTitle: String,
    ) : MyFilterRecyclerViewItem()

    class Color(
        val colorList: List<Int>
    ) : MyFilterRecyclerViewItem()
}