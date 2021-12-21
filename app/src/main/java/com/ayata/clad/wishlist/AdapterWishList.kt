package com.ayata.clad.wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.databinding.ItemRecyclerWishlistBinding
import com.ayata.clad.product.ModelProduct

class AdapterWishList(
    var productList: List<ModelProduct>,
) : RecyclerView.Adapter<AdapterWishList.ViewHolder>() {
    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: ItemRecyclerWishlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun clickView(item: ModelProduct) {
            binding.image.setOnClickListener {
                itemProductClick?.let { function ->
                    function(item)
                }
            }
            binding.ivSetting.setOnClickListener {
                itemSettingClick?.let { function ->
                    function(item)
                }
            }
        }
    }

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerWishlistBinding.inflate(
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
            with(productList[position]) {
                holder.clickView(productList[position])
            }
        }
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return productList.size
    }

    private var itemProductClick: ((ModelProduct) -> Unit)? = null
    fun setProductClickListener(listener: ((ModelProduct) -> Unit)) {
        itemProductClick = listener
    }

    private var itemSettingClick: ((ModelProduct) -> Unit)? = null
    fun setSettingClickListener(listener: ((ModelProduct) -> Unit)) {
        itemSettingClick = listener
    }
}