package com.ayata.clad.filter.filterdialog

import java.io.Serializable

data class ModelFilterContent(val title: String, var isSelected: Boolean) : Serializable
sealed class MyFilterContentViewItem {
   data class SingleChoice(
        val title: String,
        var isSelected: Boolean,
        val slug: String?=null
    ) : MyFilterContentViewItem()

   data class MultipleChoice(
        val title: String, var isSelected: Boolean,
        val slug: String?=null,
        val id:Int=0,
    ) : MyFilterContentViewItem()
   data class MultipleChoiceColor(
        val id:Int,
        val title: String, var isSelected: Boolean,var colorHex:String?="#000000",

    ) : MyFilterContentViewItem()
   data class Title(
        val title: String
    ) : MyFilterContentViewItem()
}