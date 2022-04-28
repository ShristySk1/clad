package com.ayata.clad.utils

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import com.ayata.clad.R

abstract class AbstractEmptyError {
    fun showEmpty(title: String, it: String, context: Context, parentlayout: ViewGroup) {
        MyLayoutInflater().onAddField(
            context,
            parentlayout,
            R.layout.layout_error,
            Constants.EMPTY_LIST,
            title,
            it
        )
    }

    fun showError(title: String, it: String, context: Context, parentlayout: ViewGroup) {
        MyLayoutInflater().onAddField(
            context,
            parentlayout,
            R.layout.layout_error,
            Constants.ERROR_SERVER,
            title,
            it
        )
    }

    fun hideError(parentlayout: ViewGroup) {
        if (parentlayout.findViewById<LinearLayout>(R.id.layout_root) != null) {
            MyLayoutInflater().onDelete(
                parentlayout,
                parentlayout.findViewById(R.id.layout_root)
            )
        }
    }
}