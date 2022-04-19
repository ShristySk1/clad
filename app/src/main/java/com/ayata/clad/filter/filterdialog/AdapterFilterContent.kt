package com.ayata.clad.filter.filterdialog

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ayata.clad.R
import com.ayata.clad.databinding.ItemRecyclerFilterDialogcontentTypeColorBinding
import com.ayata.clad.databinding.ItemRecyclerFilterDialogcontentTypeText2Binding
import com.ayata.clad.databinding.ItemRecyclerFilterDialogcontentTypeTextBinding

internal class AdapterFilterContent(
    private var context: Context?,
    private var listItems: List<MyFilterContentViewItem>
) : RecyclerView.Adapter<AdapterFilterContent.HomeRecyclerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerViewHolder {

        return when (viewType) {
            R.layout.item_recycler_filter_dialogcontent_type_text -> HomeRecyclerViewHolder.SingleViewHolder(
                ItemRecyclerFilterDialogcontentTypeTextBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_recycler_filter_dialogcontent_type_text2 -> HomeRecyclerViewHolder.MultipleViewHolder(
                ItemRecyclerFilterDialogcontentTypeText2Binding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_recycler_filter_dialogcontent_type_color -> HomeRecyclerViewHolder.MultipleColorViewHolder(
                ItemRecyclerFilterDialogcontentTypeColorBinding.inflate(
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
            is HomeRecyclerViewHolder.MultipleColorViewHolder -> holder.bind(
                listItems[position] as MyFilterContentViewItem.MultipleChoiceColor,
                itemFilterContentMultipleColorClick
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listItems[position]) {
            is MyFilterContentViewItem.SingleChoice -> R.layout.item_recycler_filter_dialogcontent_type_text
            is MyFilterContentViewItem.MultipleChoice -> R.layout.item_recycler_filter_dialogcontent_type_text2
            is MyFilterContentViewItem.MultipleChoiceColor -> R.layout.item_recycler_filter_dialogcontent_type_color
        }
    }


    override fun getItemCount(): Int {
        return listItems.size
    }

    //viewholder
    sealed class HomeRecyclerViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //        //color
        class MultipleColorViewHolder(private val binding: ItemRecyclerFilterDialogcontentTypeColorBinding) :
            HomeRecyclerViewHolder(binding) {
            fun bind(
                item: MyFilterContentViewItem.MultipleChoiceColor,
                itemFilterContentClick: ((MyFilterContentViewItem.MultipleChoiceColor) -> Unit)?
            ) {
                with(item) {
                    binding.title.text = item.title
                    binding.color.apply {
                        setColorFilter(Color.parseColor(item.colorHex))
                    }
                    binding.checkBox.isChecked = item.isSelected
                    itemView.setOnClickListener {
                        itemFilterContentClick?.let { function ->
                            function(item)
                        }
                    }
                }
            }
        }

        class SingleViewHolder(private val binding: ItemRecyclerFilterDialogcontentTypeTextBinding) :
            HomeRecyclerViewHolder(binding) {

            fun bind(
                item: MyFilterContentViewItem.SingleChoice,
                itemFilterContentClick: ((MyFilterContentViewItem.SingleChoice) -> Unit)?
            ) {
                with(item) {
                    binding.title.text = item.title
                    binding.checkBox.isChecked = item.isSelected
                    itemView.setOnClickListener {
                        itemFilterContentClick?.let { function ->
                            function(item)
                        }
                    }
                }
            }
        }


        class MultipleViewHolder(private val binding: ItemRecyclerFilterDialogcontentTypeText2Binding) :
            HomeRecyclerViewHolder(binding) {
            fun bind(
                item: MyFilterContentViewItem.MultipleChoice,
                itemFilterColorContentClick: ((MyFilterContentViewItem.MultipleChoice) -> Unit)?
            ) {
                with(item) {
                    binding.title.text = item.title
                    binding.checkBox.isChecked = item.isSelected
                    binding.root.setOnClickListener {
                        itemFilterColorContentClick?.let { function ->
                            function(item)
                        }
                    }
                }
            }
        }


    }

    private var itemFilterContentSingleClick: ((MyFilterContentViewItem.SingleChoice) -> Unit)? =
        null
    private var itemFilterContentMultipleClick: ((MyFilterContentViewItem.MultipleChoice) -> Unit)? =
        null

    fun setCircleClickListener(listener: ((MyFilterContentViewItem.SingleChoice) -> Unit)) {
        itemFilterContentSingleClick = listener
    }

    fun setFilterContentMultipleClickListener(listener: ((MyFilterContentViewItem.MultipleChoice) -> Unit)) {
        itemFilterContentMultipleClick = listener
    }

    private var itemFilterContentMultipleColorClick: ((MyFilterContentViewItem.MultipleChoiceColor) -> Unit)? =
        null

    fun setFilterContentMultipleColorClickListener(listener: ((MyFilterContentViewItem.MultipleChoiceColor) -> Unit)) {
        itemFilterContentMultipleColorClick = listener
    }

}