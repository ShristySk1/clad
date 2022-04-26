package com.ayata.clad.product.reviews.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.databinding.ItemRecyclerReviewBinding
import com.ayata.clad.home.response.SingleReview
import com.ayata.clad.profile.reviews.adapter.AdapterImageViewType
import com.ayata.clad.profile.reviews.adapter.DataModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

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
                initRecyclerviewImage(binding.rvPhotos,image_url.map {
                    DataModel.ImageOnly(it.id,it.imageUrl)
                }
                )
                binding.textDescription.text=review?:""
                    if ( binding.textDescription.text.isEmpty()) {
                        binding.textDescription.visibility=(View.GONE);
                    }


            }
        }
    }
    private fun initRecyclerviewImage(rvPhotos: RecyclerView, listImage: List<DataModel.ImageOnly>) {
        if(listImage.size>0) {
            val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
                alignItems = AlignItems.FLEX_START
            }
            val adapterImage = AdapterImageViewType(listImage.toMutableList())
            rvPhotos.apply {
                layoutManager = flexboxLayoutManager
                adapter = adapterImage
            }
            adapterImage.setReviewClickListener { image, position ->
                itemReviewClick?.let {
                    it(image,position)
                }
            }
        }else{
            rvPhotos.visibility=View.GONE
        }
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return listItems.size
    }

    private var itemReviewClick: ((List<DataModel.ImageOnly>,Int) -> Unit)? = null
    fun setReviewClickListener(listener: ((List<DataModel.ImageOnly>,Int) -> Unit)) {
        itemReviewClick = listener
    }

}