package com.ayata.clad.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ayata.clad.R
import com.ayata.clad.databinding.ItemRecyclerFilterBinding
import com.ayata.clad.databinding.ItemRecyclerFilterColorBinding
import com.ayata.clad.filter.color.AdapterFilterColor

class AdapterFilter(data: List<ModelFilter>) :
    RecyclerView.Adapter<AdapterFilter.HomeRecyclerViewHolder>() {
    var items = listOf<MyFilterRecyclerViewItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerViewHolder {

        return when (viewType) {
            R.layout.item_recycler_filter -> HomeRecyclerViewHolder.TitleViewHolder(
                ItemRecyclerFilterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_recycler_filter_color -> HomeRecyclerViewHolder.ColorViewHolder(
                ItemRecyclerFilterColorBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid ViewType Provided")
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: HomeRecyclerViewHolder, position: Int) {
        when (holder) {
            is HomeRecyclerViewHolder.TitleViewHolder -> holder.bind(
                items[position] as MyFilterRecyclerViewItem.Title,
                itemFilterClick
            )
            is HomeRecyclerViewHolder.ColorViewHolder -> holder.bind(items[position] as MyFilterRecyclerViewItem.Color, itemFilterColorClick)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MyFilterRecyclerViewItem.Title -> R.layout.item_recycler_filter
            is MyFilterRecyclerViewItem.Color -> R.layout.item_recycler_filter_color
        }
    }

    sealed class HomeRecyclerViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        class TitleViewHolder(private val binding: ItemRecyclerFilterBinding) :
            HomeRecyclerViewHolder(binding) {

            fun bind(
                item: MyFilterRecyclerViewItem.Title,
                itemFilterClick: ((MyFilterRecyclerViewItem.Title,Int) -> Unit)?
            ) {
                with(item) {
                    binding.content.text = item.title
                    binding.content2.text = item.subTitle
                    itemView.setOnClickListener {
                        itemFilterClick?.let { function ->
                            function(item,adapterPosition)
                        }
                    }
                }
            }
        }


        class ColorViewHolder(private val binding: ItemRecyclerFilterColorBinding) :
            HomeRecyclerViewHolder(binding) {
            fun bind(item: MyFilterRecyclerViewItem.Color,itemFilterClick: ((MyFilterRecyclerViewItem.Color,Int) -> Unit)?) {
                with(item) {
                    binding.content.text = item.title
                    binding.rvColors.apply {
                        layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = AdapterFilterColor(item.colorList)
                        isLayoutFrozen=true
                    }
                    itemView.setOnClickListener {
                        itemFilterClick?.let { function ->
                            function(item,adapterPosition)
                        }
                    }
                }
            }
        }


    }

    private var itemFilterClick: ((MyFilterRecyclerViewItem.Title,Int) -> Unit)? = null
    fun setFilterClickListener(listener: ((MyFilterRecyclerViewItem.Title,Int) -> Unit)) {
        itemFilterClick = listener
    }

    private var itemFilterColorClick: ((MyFilterRecyclerViewItem.Color,Int) -> Unit)? = null
    fun setFilterColorClickListener(listener: ((MyFilterRecyclerViewItem.Color,Int) -> Unit)) {
        itemFilterColorClick = listener
    }


}