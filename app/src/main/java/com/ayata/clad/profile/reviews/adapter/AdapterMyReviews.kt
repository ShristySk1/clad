package com.ayata.clad.product.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.databinding.ItemRecyclerToBeReviewedBinding
import com.ayata.clad.profile.reviews.model.ModelReview


class AdapterMyReviews(val context:Context,
                       var myReviewList: List<ModelReview>,
) : RecyclerView.Adapter<AdapterMyReviews.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRecyclerToBeReviewedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun clickView(item: ModelReview) {
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
    private var itemReviewClick: ((ModelReview) -> Unit)? = null
    fun setReviewClickListener(listener: ((ModelReview) -> Unit)) {
        itemReviewClick = listener
    }
}