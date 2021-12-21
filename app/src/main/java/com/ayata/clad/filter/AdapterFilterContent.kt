package com.ayata.clad.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R

internal class AdapterFilterContent(
    private var context: Context?,
    private var listItems: List<ModelFilterContent>
) : RecyclerView.Adapter<AdapterFilterContent.MyViewHolder>() {


    internal inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textView = itemView.findViewById<TextView>(R.id.title)
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)

        fun clickView(item: ModelFilterContent) {
            textView.text = item.title
            itemView.setOnClickListener {
                itemCircleClick?.let { function ->
                    function(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_recycler_filter_type_text, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = listItems[position]

        holder.textView.text = item.title
        if (item.isSelected) {
            holder.checkBox.isChecked = true
        } else {
            holder.checkBox.isChecked = false
        }
        holder.clickView(item)

    }


    override fun getItemCount(): Int {
        return listItems.size
    }

    private var itemCircleClick: ((ModelFilterContent) -> Unit)? = null
    fun setCircleClickListener(listener: ((ModelFilterContent) -> Unit)) {
        itemCircleClick = listener
    }

}