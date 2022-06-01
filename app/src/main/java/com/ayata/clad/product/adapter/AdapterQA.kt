package com.ayata.clad.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.databinding.ItemRecyclerQuestionAnswer2Binding
import com.ayata.clad.product.ModelColor

class AdapterQA(
    var colorList: List<String>, var type: Int, var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdapterQA.ViewHolder>() {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: ItemRecyclerQuestionAnswer2Binding) :
        RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerQuestionAnswer2Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    // bind the items with each item
    // of the list languageList
    // which than will be
    // shown in recycler view
    // to keep it simple we are
    // not setting any image data to view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            when (type) {
                0 -> {
                    binding.layoutQa.background= null
                }
                else -> {
                    binding.layoutQa.background= ContextCompat.getDrawable(holder.binding.date.context, R.drawable.background_outline_bottom)

                }
            }
        }
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return colorList.size
    }

    interface OnItemClickListener {
        fun onColorClicked(color: ModelColor, position: Int)
    }
}