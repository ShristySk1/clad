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
        val id:Int,
        val title: String,
        val colorList: List<MyColor>
    ) : MyFilterRecyclerViewItem()
}
data class MyColor(val colorName:String,val color: Int)