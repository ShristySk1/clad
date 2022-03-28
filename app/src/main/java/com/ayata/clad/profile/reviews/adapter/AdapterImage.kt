package com.ayata.clad.product.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.databinding.ItemRecyclerReviewImageBinding
import com.ayata.clad.profile.reviews.model.ModelReview
import com.bumptech.glide.Glide


class AdapterImage(
    val context: Context,
    var myReviewList: List<String>,
) : RecyclerView.Adapter<AdapterImage.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRecyclerReviewImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun clickView(item: String) {
            Glide.with(context).load(item).into(binding.image)
        }
    }

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerReviewImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(myReviewList[position]) {
                holder.clickView(myReviewList[position])
            }
        }
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return myReviewList.size
    }

    private var itemReviewClick: ((ModelReview) -> Unit)? = null
    fun setReviewClickListener(listener: ((ModelReview) -> Unit)) {
        itemReviewClick = listener
    }
}