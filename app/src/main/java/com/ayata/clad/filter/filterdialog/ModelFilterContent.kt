package com.ayata.clad.filter.filterdialog

import java.io.Serializable

data class ModelFilterContent(val title: String, var isSelected: Boolean) : Serializable
sealed class MyFilterContentViewItem {
    class SingleChoice(
        val title: String,
        var isSelected: Boolean,
        val slug: String?=null
    ) : MyFilterContentViewItem()

    class MultipleChoice(
        val title: String, var isSelected: Boolean,
        val slug: String?=null,
        val id:Int=0,
    ) : MyFilterContentViewItem()
    class MultipleChoiceColor(
        val id:Int,
        val title: String, var isSelected: Boolean,var colorHex:String?="#000000",

    ) : MyFilterContentViewItem()
    class Title(
        val title: String
    ) : MyFilterContentViewItem()
}