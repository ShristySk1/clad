package com.ayata.clad.wishlist.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.databinding.ItemRecyclerWishlistBinding
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.wishlist.response.get.Wishlist
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class AdapterWishList(
    val context: Context,
    var productList: List<Wishlist>
) : RecyclerView.Adapter<AdapterWishList.ViewHolder>() {
    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: ItemRecyclerWishlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun clickView(item: Wishlist) {
            binding.progressBar.visibility = View.VISIBLE
            Glide.with(binding.image.context)
                .load(item.product.imageUrl[0])
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        @Nullable e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }
                })
                .error(R.drawable.shoes)
                .into(binding.image)
            if (PreferenceHandler.getCurrency(context)
                    .equals(context!!.getString(R.string.npr_case), true)
            ) {
                //npr
                binding.price.text = "${context!!.getString(R.string.rs)} ${item.product.price}"
            } else {
                //usd
                binding.price.text = "${context!!.getString(R.string.usd)} ${item.product.price}"
            }

            binding.image.setOnClickListener {
                itemProductClick?.let { function ->
                    function(item)
                }
            }
            binding.title.setOnClickListener {
                binding.image.performClick()
            }
            binding.ivSetting.setOnClickListener {
                itemSettingClick?.let { function ->
                    function(item)
                }
            }
//            if (item.product.is_in_cart) {
//
//            } else {
//
//            }
            binding.imageView4.setOnClickListener {
                itemBagClick?.let { function ->
                    function(item)
                }
            }
            binding.name.text = item.product.name
            binding.company.text = item.product.vendor

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

    private var itemProductClick: ((Wishlist) -> Unit)? = null
    fun setProductClickListener(listener: ((Wishlist) -> Unit)) {
        itemProductClick = listener
    }

    private var itemSettingClick: ((Wishlist) -> Unit)? = null
    fun setSettingClickListener(listener: ((Wishlist) -> Unit)) {
        itemSettingClick = listener
    }

    private var itemBagClick: ((Wishlist) -> Unit)? = null
    fun setBagClickListener(listener: ((Wishlist) -> Unit)) {
        itemBagClick = listener
    }

}