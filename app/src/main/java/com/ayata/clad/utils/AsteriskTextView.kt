package com.ayata.clad.utils

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


class AsteriskTextView : AppCompatTextView {
    var colored = "*"
    var builder = SpannableStringBuilder()

    constructor(context: Context) : super(context) {
        updateText()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        updateText()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        updateText()
    }

    fun updateText() {
        try {
            builder.append(text) //get string
            builder.append(" ") // add space between text and start
            val start = builder.length
            builder.append(colored)
            val end = builder.length
            builder.setSpan(
                ForegroundColorSpan(Color.RED), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text = builder
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}