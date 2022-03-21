package com.ayata.clad.view_all.paging

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.databinding.ItemRecyclerProductlistBinding
import com.ayata.clad.databinding.RecyclerViewAllProductBinding
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.utils.PreferenceHandler
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class ProductDetailViewAllAdapter(private val listener:onItemClickListener) : PagingDataAdapter<ProductDetail, ProductDetailViewAllAdapter.ProductDetailViewHolder>(PHOTO_COMPARATOR) {
    inner class ProductDetailViewHolder(private val binding: RecyclerViewAllProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position=bindingAdapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    val item=(getItem(position))
                    item?.let {
                        listener.onItemClick(item,position)
                    }
                }
            }
            binding.iconWish.setOnClickListener {
                val position=bindingAdapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    val item=(getItem(position))
                    item?.let {
                        listener.onWishListClick(item,position)
                    }
                }
//                onItemClickListener.onWishListClicked(itemList!![adapterPosition]!!,adapterPosition)

            }
        }

        fun bind(item: ProductDetail) {
//            binding.apply {
//                Glide.with(itemView).load(photo.urls.regular)
//                    .centerCrop()
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .error(R.drawable.splashimage)
//                    .into(imageView)
//                textViewUserName.text = photo.user.username
//            }
            Log.d("getAllTest", "bind: "+item.name);
            val context=binding.name.context
            binding.apply {
                 name.text = item!!.name
                company.text = item!!.vendor
                if(PreferenceHandler.getCurrency(name.context).equals(name.context.getString(R.string.npr_case),true)){
                    price.text="${context!!.getString(R.string.rs)} ${item.price}"
                }else{
                    price.text="${context!!.getString(R.string.usd)} ${item.dollar_price}"
                }

                progressBar.visibility = View.VISIBLE
                Glide.with(context!!)
                    .load(item.imageUrl[0])
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            @Nullable e: GlideException?,
                            model: Any,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any,
                            target: Target<Drawable?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE
                            return false
                        }
                    })
                    .error(R.drawable.shoes)
                    .into(image)

                if(item.isInWishlist){
                    Glide.with(context!!).load(R.drawable.ic_heart_filled).into(iconWish)
                }else{
                    iconWish.setImageResource(R.drawable.ic_heart_outline)
                    iconWish.drawable.setTint(ContextCompat.getColor(context!!,R.color.colorBlack))
                }
            }


        }
    }

    override fun onBindViewHolder(holder: ProductDetailViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailViewHolder {
        val binding =
            RecyclerViewAllProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductDetailViewHolder(binding)
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<ProductDetail>() {
            override fun areItemsTheSame(oldItem: ProductDetail, newItem: ProductDetail): Boolean {
                return oldItem.productId == newItem.productId
            }

            override fun areContentsTheSame(
                oldItem: ProductDetail,
                newItem: ProductDetail
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    interface onItemClickListener {
        fun onItemClick(product: ProductDetail,position: Int)
        fun onWishListClick(product: ProductDetail,position: Int)
    }
}