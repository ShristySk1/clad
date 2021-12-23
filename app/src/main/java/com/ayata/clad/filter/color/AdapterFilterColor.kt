package com.ayata.clad.filter.color

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.databinding.ItemRecyclerFilterColorRecyclerBinding
import com.ayata.clad.filter.MyColor

class AdapterFilterColor(
    var colorList: List<MyColor>,
) : RecyclerView.Adapter<AdapterFilterColor.ViewHolder>() {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: ItemRecyclerFilterColorRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerFilterColorRecyclerBinding.inflate(
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
                    setColorFilter(ContextCompat.getColor(context, colorList[position].color));
                }
                binding.colorName.text = colorList[position].colorName
            }
        }
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return colorList.size
    }
}