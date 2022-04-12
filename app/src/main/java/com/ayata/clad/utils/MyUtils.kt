package com.ayata.clad.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ayata.clad.R
import com.ayata.clad.databinding.DialogShoppingSizeBinding
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.model.ModelCheckout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

fun Context.copyToClipboard(text: CharSequence){
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    val clip = ClipData.newPlainText("label",text)
    clipboard?.setPrimaryClip(clip)
}
inline fun View.snack(message:String, left:Int = 10, top:Int = 10, right:Int = 10, bottom:Int = 10, duration:Int = Snackbar.LENGTH_SHORT){
    Snackbar.make(this, message, duration).apply {

        val params = CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT )
        params.setMargins(left, top, right, bottom)
        params.gravity = Gravity.BOTTOM
        params.anchorGravity = Gravity.BOTTOM
            setActionTextColor(Color.WHITE)
        view.layoutParams = params
        show()
    }
}
 fun Context.showToast(message: String, isSuccess: Boolean,yOffset:Int=0,xOffset:Int=0) {
    val toast = Toast(this)
    val view: View = LayoutInflater.from(this)
        .inflate(R.layout.custom_toast, null)
    val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
    val ivImage = view.findViewById<ImageView>(R.id.ivImage)
    val cardView: CardView = view.findViewById(R.id.cardBackground)
    tvMessage.text = message
    if (isSuccess) {
        cardView.setCardBackgroundColor(this.resources.getColor(R.color.colorGreenDark))
        ivImage.setImageResource(R.drawable.ic_success)
    } else {
        cardView.setCardBackgroundColor(this.resources.getColor(R.color.colorRedDark))
        ivImage.setImageResource(R.drawable.ic_info)
    }
    toast.setView(view)
    toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, xOffset, yOffset)
    toast.show()
}
fun String.removeDoubleQuote() = this?.let { this.replace("\"", "") }
fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


