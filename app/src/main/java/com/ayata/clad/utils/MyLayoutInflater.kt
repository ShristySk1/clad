package com.ayata.clad.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.contains
import com.ayata.clad.R

class MyLayoutInflater  {
    fun onDelete(parentView: ViewGroup, childView: View) {
        parentView!!.removeView(childView)

    }

    fun onAddField(context: Context, parentView: ViewGroup, childLayout: Int,myImage: Int,title:String,subtitle:String) {
        if (parentView.findViewById<LinearLayout>(R.id.layout_root) != null) {
            MyLayoutInflater().onDelete(
                parentView,
                parentView.findViewById(R.id.layout_root)
            )
        }
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(childLayout, parentView)
        val images=rowView.findViewById<ImageView>(R.id.error_image)
        val titles=rowView.findViewById<TextView>(R.id.error_title)
        val subTitles=rowView.findViewById<TextView>(R.id.error_description)
        images.setImageResource(myImage)
        subTitles.setText(subtitle)
        titles.setText(title)
        if (parentView.contains(rowView)) {
            parentView.removeView(rowView) // <- fix
            parentView.addView(rowView)

        }

    }

}