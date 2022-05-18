package com.ayata.clad.filter

import com.ayata.clad.productlist.response.MySize
import java.io.Serializable

data class ModelFilter(
    val title: String,
    val subTitle: String,
    val colorList: List<Int>
) : Serializable

sealed class MyFilterRecyclerViewItem {
    data class Title(
        var id:Int,
        val title: String,
        var subTitle: String,
        var slug: String?=null,
        var listOfIdsOfSlug:String?=null
    ) : MyFilterRecyclerViewItem(){
        override fun toString(): String {
            return "Title(title='$subTitle')"
        }
    }

    data class Color(
        val id:Int,
        val title: String,
        var colorList: List<MyColor>
    ) : MyFilterRecyclerViewItem(){
        override fun toString(): String {
            return "Title(title='$title')"
        }
    }
}
data class MyColor(val colorName:String,val color: String?=null,val id:Int?=null)
