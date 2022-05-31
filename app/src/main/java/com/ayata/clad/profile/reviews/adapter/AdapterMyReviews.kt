package com.ayata.clad.product.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.databinding.ItemRecyclerToBeReviewedBinding
import com.ayata.clad.profile.reviews.model.Review
import com.bumptech.glide.Glide


class AdapterMyReviews(
    val context: Context,
    var myReviewList: List<Review>,
) : RecyclerView.Adapter<AdapterMyReviews.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRecyclerToBeReviewedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun clickView(item: Review) {
            if (item.reviewDetails.isReviewed) {
                binding.ratingBar.visibility = View.VISIBLE
                binding.tvReview.text = "Edit"
                binding.ratingBar.rating = item.reviewDetails.rate.toFloat()
            }
            binding.price.text = "Rs. " + item.product.price.toString()
            binding.name.text = item.product.name
            binding.itemId.text = "Item ID: ${item.orderCode}"
            Glide.with(context).load(item.product.image_url).into(binding.image)
            binding.tvDate.text = "Purchased on " + item.orderDate
            binding.description.text =
                "${item.product.size?.let { "Size: " + it + "/ " } ?: run { "" }}Colour: ${item.product.color} / Qty: ${item.product.quantity}"
            binding.tvReview.setOnClickListener {
                itemReviewClick?.let {
                    it(item)
                }
            }
        }
    }

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerToBeReviewedBinding.inflate(
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

    private var itemReviewClick: ((Review) -> Unit)? = null
    fun setReviewClickListener(listener: ((Review) -> Unit)) {
        itemReviewClick = listener
    }
}