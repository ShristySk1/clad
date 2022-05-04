package com.ayata.clad.utils

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable

import android.widget.ImageButton
/**
 * Sets the image button to the given state and grays-out the icon.
 *
 * @param ctxt The context
 * @param enabled The state of the button
 * @param item The button item to modify
 * @param iconResId The button's icon ID
 */

fun setImageButtonEnabled(
    ctxt: Context, enabled: Boolean,
    item: ImageButton, iconResId: Int
) {
    item.isEnabled = enabled
    val originalIcon: Drawable = ctxt.getResources().getDrawable(iconResId)
    val icon = if (enabled) originalIcon else convertDrawableToGrayScale(originalIcon)!!
    item.setImageDrawable(icon)
}

/**
 * Mutates and applies a filter that converts the given drawable to a Gray
 * image. This method may be used to simulate the color of disable icons in
 * Honeycomb's ActionBar.
 *
 * @return a mutated version of the given drawable with a color filter applied.
 */
fun convertDrawableToGrayScale(drawable: Drawable?): Drawable? {
    if (drawable == null) return null
    val res = drawable.mutate()
    res.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN)
    return res
}