package com.ayata.clad.filter.filterdialog

import java.io.Serializable

data class ModelFilterContent(val title: String, var isSelected: Boolean) : Serializable
sealed class MyFilterContentViewItem {
    class SingleChoice(
        val title: String, var isSelected: Boolean
    ) : MyFilterContentViewItem()

    class MultipleChoice(
        val title: String, var isSelected: Boolean
    ) : MyFilterContentViewItem()
}