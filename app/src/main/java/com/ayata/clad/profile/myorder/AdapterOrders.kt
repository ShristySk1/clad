package com.ayata.clad.profile.myorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ayata.clad.R
import com.ayata.clad.databinding.ItemRecyclerProfileOrdersTypeDateBinding
import com.ayata.clad.databinding.ItemRecyclerProfileOrdersTypeLineBinding
import com.ayata.clad.databinding.ItemRecyclerProfileOrdersTypeProductBinding
import com.ayata.clad.profile.ModelOrder
import com.ayata.clad.profile.MyOrderRecyclerViewItem

class AdapterOrders(data: List<ModelOrder>) :
    RecyclerView.Adapter<AdapterOrders.HomeRecyclerViewHolder>() {
    var items = listOf<MyOrderRecyclerViewItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerViewHolder {

        return when (viewType) {
            R.layout.item_recycler_profile_orders_type_date -> HomeRecyclerViewHolder.TitleViewHolder(
                ItemRecyclerProfileOrdersTypeDateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_recycler_profile_orders_type_product -> HomeRecyclerViewHolder.ProductViewHolder(
                ItemRecyclerProfileOrdersTypeProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_recycler_profile_orders_type_line -> HomeRecyclerViewHolder.DividerViewHolder(
                ItemRecyclerProfileOrdersTypeLineBinding.inflate(
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
            is HomeRecyclerViewHolder.TitleViewHolder -> holder.bind(items[position] as MyOrderRecyclerViewItem.Title)
            is HomeRecyclerViewHolder.ProductViewHolder -> holder.bind(items[position] as MyOrderRecyclerViewItem.Product)
            is HomeRecyclerViewHolder.DividerViewHolder ->  holder.bind(items[position] as MyOrderRecyclerViewItem.Divider)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MyOrderRecyclerViewItem.Title -> R.layout.item_recycler_profile_orders_type_date
            is MyOrderRecyclerViewItem.Product -> R.layout.item_recycler_profile_orders_type_product
            is MyOrderRecyclerViewItem.Divider -> R.layout.item_recycler_profile_orders_type_line
        }
    }

    sealed class HomeRecyclerViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        class TitleViewHolder(private val binding: ItemRecyclerProfileOrdersTypeDateBinding) :
            HomeRecyclerViewHolder(binding) {
            fun bind(item: MyOrderRecyclerViewItem.Title) {
                with(item) {

                }
            }
        }


        class ProductViewHolder(private val binding: ItemRecyclerProfileOrdersTypeProductBinding) :
            HomeRecyclerViewHolder(binding) {
            fun bind(item: MyOrderRecyclerViewItem.Product) {
                with(item){

                }
            }
        }

        class DividerViewHolder(private val binding: ItemRecyclerProfileOrdersTypeLineBinding) :
            HomeRecyclerViewHolder(binding) {
            fun bind(item: MyOrderRecyclerViewItem.Divider) {
                with(item){
                }
            }
        }

    }


}