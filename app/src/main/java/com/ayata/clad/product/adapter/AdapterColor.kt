package com.ayata.clad.product.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.databinding.ItemRecyclerColorChooseBinding
import com.ayata.clad.product.ModelColor

class AdapterColor(
    var colorList: List<ModelColor>, var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdapterColor.ViewHolder>() {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: ItemRecyclerColorChooseBinding) :
        RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerColorChooseBinding.inflate(
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
            with(colorList[position]) {
                binding.color.apply {
                    setColorFilter(Color.parseColor(colorList[position].colorHex))
                }
                binding.root.setOnClickListener {
                    onItemClickListener.onColorClicked(colorList[position], position)
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