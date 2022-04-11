package com.ayata.clad.view_all.paging

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.databinding.RecyclerViewAllBrandBinding
import com.ayata.clad.home.response.Brand
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class BrandViewAllAdapter(val listener: onItemClickListener) :
    PagingDataAdapter<Brand, BrandViewAllAdapter.BrandViewHolder>(PHOTO_COMPARATOR) {
    inner class BrandViewHolder(private val binding: RecyclerViewAllBrandBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = (getItem(position))
                    item?.let {
                        listener.onBrandClick(
                            getItem(bindingAdapterPosition),
                            bindingAdapterPosition
                        )

                    }
                }
            }
        }

        fun bind(item: Brand) {
            binding.apply {
                name.text = item!!.name
                progressBar.visibility = View.VISIBLE
                Glide.with(name.context)
                    .load(item.brandImage)
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

            }
        }
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val binding =
            RecyclerViewAllBrandBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BrandViewHolder(binding)
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<Brand>() {
            override fun areItemsTheSame(oldItem: Brand, newItem: Brand): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Brand,
                newItem: Brand
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    interface onItemClickListener {
        fun onBrandClick(product: Brand?, position: Int)
    }
}