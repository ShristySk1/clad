package com.ayata.clad.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ayata.clad.R
import com.ayata.clad.databinding.ItemRecyclerFilterTypeText2Binding
import com.ayata.clad.databinding.ItemRecyclerFilterTypeTextBinding

internal class AdapterFilterContent(
    private var context: Context?,
    private var listItems: List<MyFilterContentViewItem>
) : RecyclerView.Adapter<AdapterFilterContent.HomeRecyclerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerViewHolder {

        return when (viewType) {
            R.layout.item_recycler_filter_type_text -> HomeRecyclerViewHolder.SingleViewHolder(
                ItemRecyclerFilterTypeTextBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_recycler_filter_type_text2 -> HomeRecyclerViewHolder.MultipleViewHolder(
                ItemRecyclerFilterTypeText2Binding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid ViewType Provided")
        }
    }


    override fun onBindViewHolder(holder: HomeRecyclerViewHolder, position: Int) {
        when (holder) {
            is HomeRecyclerViewHolder.SingleViewHolder -> holder.bind(
                listItems[position] as MyFilterContentViewItem.SingleChoice,
                itemFilterContentSingleClick
            )
            is HomeRecyclerViewHolder.MultipleViewHolder -> holder.bind(
                listItems[position] as MyFilterContentViewItem.MultipleChoice,
                itemFilterContentMultipleClick
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listItems[position]) {
            is MyFilterContentViewItem.SingleChoice -> R.layout.item_recycler_filter_type_text
            is MyFilterContentViewItem.MultipleChoice -> R.layout.item_recycler_filter_type_text2
        }
    }


    override fun getItemCount(): Int {
        return listItems.size
    }

    //viewholder
    sealed class HomeRecyclerViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        class SingleViewHolder(private val binding: ItemRecyclerFilterTypeTextBinding) :
            HomeRecyclerViewHolder(binding) {

            fun bind(
                item: MyFilterContentViewItem.SingleChoice,
                itemFilterContentClick: ((MyFilterContentViewItem.SingleChoice) -> Unit)?
            ) {
                with(item) {
                    binding.title.text = item.title
                    if (item.isSelected) {
                        binding.checkBox.isChecked = true
                    } else {
                        binding.checkBox.isChecked = false
                    }
                    itemView.setOnClickListener {
                        itemFilterContentClick?.let { function ->
                            function(item)
                        }
                    }
                }
            }
        }


        class MultipleViewHolder(private val binding: ItemRecyclerFilterTypeText2Binding) :
            HomeRecyclerViewHolder(binding) {
            fun bind(
                item: MyFilterContentViewItem.MultipleChoice,
                itemFilterContentClick: ((MyFilterContentViewItem.MultipleChoice) -> Unit)?
            ) {
                with(item) {
                    binding.title.text = item.title
                    if (item.isSelected) {
                        binding.checkBox.isChecked = true
                    } else {
                        binding.checkBox.isChecked = false
                    }
                    itemView.setOnClickListener {
                        itemFilterContentClick?.let { function ->
                            function(item)
                        }
                    }
                }
            }
        }


    }

    private var itemFilterContentSingleClick: ((MyFilterContentViewItem.SingleChoice) -> Unit)? = null
    private var itemFilterContentMultipleClick: ((MyFilterContentViewItem.MultipleChoice) -> Unit)? =
        null

    fun setCircleClickListener(listener: ((MyFilterContentViewItem.SingleChoice) -> Unit)) {
        itemFilterContentSingleClick = listener
    }

    fun setFilterContentMultipleClickListener(listener: ((MyFilterContentViewItem.MultipleChoice) -> Unit)) {
        itemFilterContentMultipleClick = listener
    }

}