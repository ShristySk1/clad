package com.ayata.clad.utils

import android.content.Context
import android.view.ViewGroup

class Caller : AbstractEmptyError() {
    fun error(title: String, it: String, context: Context, parentlayout: ViewGroup) {
        showError(title, it, context, parentlayout)
    }

    fun empty(title: String, it: String, context: Context, parentlayout: ViewGroup) {
        showEmpty(title, it, context, parentlayout)
    }

    fun hideErrorEmpty(parentlayout: ViewGroup) {
        hideError(parentlayout)
    }
}