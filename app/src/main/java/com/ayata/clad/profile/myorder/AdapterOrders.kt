package com.ayata.clad.profile.myorder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ayata.clad.R
import com.ayata.clad.databinding.ItemRecyclerProfileOrdersTypeDateBinding
import com.ayata.clad.databinding.ItemRecyclerProfileOrdersTypeLineBinding
import com.ayata.clad.databinding.ItemRecyclerProfileOrdersTypeProductBinding
import com.ayata.clad.profile.ModelOrder
import com.ayata.clad.profile.MyOrderRecyclerViewItem
import com.ayata.clad.utils.PreferenceHandler
import com.bumptech.glide.Glide

class AdapterOrders(val context: Context, data: List<ModelOrder>) :
    RecyclerView.Adapter<AdapterOrders.HomeRecyclerViewHolder>() {
    var items = listOf<MyOrderRecyclerViewItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerViewHolder {
        return when (viewType) {
            R.layout.item_recycler_profile_orders_type_date -> HomeRecyclerViewHolder.TitleViewHolder(
                context,
                ItemRecyclerProfileOrdersTypeDateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_recycler_profile_orders_type_product -> HomeRecyclerViewHolder.ProductViewHolder(
                context,
                ItemRecyclerProfileOrdersTypeProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_recycler_profile_orders_type_line -> HomeRecyclerViewHolder.DividerViewHolder(
                context,
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
            is HomeRecyclerViewHolder.ProductViewHolder -> {
                holder.bind(items[position] as MyOrderRecyclerViewItem.Product)
                holder.itemView.setOnClickListener {
                    itemOrderClick?.let {
                        it(items.get(position) as MyOrderRecyclerViewItem.Product)
                    }
                }
            }
            is HomeRecyclerViewHolder.DividerViewHolder -> holder.bind(items[position] as MyOrderRecyclerViewItem.Divider)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MyOrderRecyclerViewItem.Title -> R.layout.item_recycler_profile_orders_type_date
            is MyOrderRecyclerViewItem.Product -> R.layout.item_recycler_profile_orders_type_product
            is MyOrderRecyclerViewItem.Divider -> R.layout.item_recycler_profile_orders_type_line
            else -> R.layout.item_recycler_profile_orders_type_line
        }
    }

    sealed class HomeRecyclerViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        class TitleViewHolder(
            val context: Context,
            private val binding: ItemRecyclerProfileOrdersTypeDateBinding
        ) :
            HomeRecyclerViewHolder(binding) {
            fun bind(item: MyOrderRecyclerViewItem.Title) {
                with(item) {
                    binding.textView29.text = date
//                    binding.textView30.text = itemId
                }
            }
        }


        class ProductViewHolder(
            val context: Context,
            private val binding: ItemRecyclerProfileOrdersTypeProductBinding
        ) :
            HomeRecyclerViewHolder(binding) {
            fun bind(item: MyOrderRecyclerViewItem.Product) {
                with(item) {
                    if (PreferenceHandler.getCurrency(context)
                            .equals(context!!.getString(R.string.npr_case), true)
                    ) {
                        binding.price.text = "${context!!.getString(R.string.rs)} ${item.priceNPR}"
                    } else {
                        binding.price.text = "${context!!.getString(R.string.usd)} ${item.priceUSD}"
                    }
                    binding.name.text = name
                    binding.itemId.text = "Item ID: ${order.products.productId}"
                    Glide.with(context).load(item.image).into(binding.image)
                    binding.description.text =
                        "${order.products.variant.size?.let { "Size: " + it + "/ " } ?: run { "" }}Colour: ${order.products.variant.color} / Qty: ${quantity}"
                    binding.orderStatus.text =
                        order.currentStatus
                            .toString()
                    //setcolor and background for order status\
                    if(order.currentStatus.contains("Delivered",ignoreCase = true)){
                        changeColor(binding.orderStatus, R.color.colorGreenLight, R.color.colorGreenDark)
                    }else if(order.currentStatus.contains("Refunded",ignoreCase = true) or order.currentStatus.contains("Cancel",ignoreCase = true)){
                        changeColor(binding.orderStatus, R.color.colorRedLight, R.color.colorRedDark)
                    }else{
                        changeColor(binding.orderStatus, R.color.colorBlueLight, R.color.colorBlueDark)
                    }

                }
            }
            private fun changeColor(stock: TextView, colorLight: Int, colorDark: Int) {
                stock.setTextColor(ContextCompat.getColor(context!!, colorDark));
                stock.background.setTint(ContextCompat.getColor(context!!, colorLight));
            }
        }

        class DividerViewHolder(
            val context: Context,
            private val binding: ItemRecyclerProfileOrdersTypeLineBinding
        ) :
            HomeRecyclerViewHolder(binding) {
            fun bind(item: MyOrderRecyclerViewItem.Divider) {
                with(item) {
                }
            }
        }

    }

    var itemOrderClick: ((MyOrderRecyclerViewItem.Product) -> Unit)? = null
    fun setitemOrderClick(listener: ((MyOrderRecyclerViewItem.Product) -> Unit)) {
        itemOrderClick = listener
    }


}