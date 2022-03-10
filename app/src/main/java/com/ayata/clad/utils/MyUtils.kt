package com.ayata.clad.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.ayata.clad.databinding.DialogShoppingSizeBinding
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.model.ModelCheckout
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Context.copyToClipboard(text: CharSequence){
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    val clip = ClipData.newPlainText("label",text)
    clipboard?.setPrimaryClip(clip)
}

