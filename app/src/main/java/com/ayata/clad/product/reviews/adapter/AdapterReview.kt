package com.ayata.clad.product.reviews.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.databinding.ItemRecyclerReviewBinding
import com.ayata.clad.home.response.SingleReview

internal class AdapterReview(
    private var context: Context?,
    private var listItems: List<SingleReview>
) : RecyclerView.Adapter<AdapterReview.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemRecyclerReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterReview.MyViewHolder {
        val binding = ItemRecyclerReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterReview.MyViewHolder, position: Int) {
        with(holder) {
            with(listItems[position]) {
                binding.comment.text = createdAt
                binding.name.text = customer
                binding.ratingBar1.rating= rate.toFloat()
                binding.textDescription.text=review
            }
        }
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return listItems.size
    }

//    private var itemBagClick: ((Wishlist) -> Unit)? = null
//    fun setBagClickListener(listener: ((Wishlist) -> Unit)) {
//        itemBagClick = listener
//    }

}