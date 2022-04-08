package com.ayata.clad.profile.reviews.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.profile.reviews.MY_PHOTO_NUMBER
import com.ayata.clad.profile.reviews.model.ModelReview
import com.bumptech.glide.Glide
import org.w3c.dom.Text

class AdapterImageViewType(private val adapterData:MutableList<DataModel>) : RecyclerView.Adapter<AdapterImageViewType.DataAdapterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataAdapterViewHolder {
        val layout = when (viewType) {
            TYPE_CAMERA -> R.layout.item_camera
            TYPE_IMAGE -> R.layout.item_recycler_review_image
            else -> throw IllegalArgumentException("Invalid type")
        }
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)

        return DataAdapterViewHolder(view)
    }
    override fun onBindViewHolder(
        holder: DataAdapterViewHolder,
        position: Int
    ) {
        holder.bind(adapterData[position])
        when(adapterData[position]){
            is DataModel.Camera->{
                Log.d("testmy", "onBindViewHolder: "+(adapterData[position] as DataModel.Camera).isEnabled);
                holder.itemView.setOnClickListener {
                itemReviewAddClick?.let {
                    it()
                }
            }}
            is DataModel.Image->{holder.itemView.findViewById<ImageView>(R.id.iv_delete).setOnClickListener {
                itemReviewDeleteClick?.let {
                    it(adapterData[position] as DataModel.Image,position)
                }
            }}
        }

    }

    override fun getItemCount(): Int = adapterData.size

    override fun getItemViewType(position: Int): Int {
        return when (adapterData[position]) {
            is DataModel.Camera -> TYPE_CAMERA
            is DataModel.Image -> TYPE_IMAGE
            else -> TYPE_CAMERA
        }
    }

    fun setData(data: List<DataModel>) {
        adapterData.apply {
            clear()
            addAll(data)
        }
    }
    fun getData()=adapterData

    companion object {
        private const val TYPE_CAMERA = 0
        private const val TYPE_IMAGE = 1

    }
    inner class DataAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private fun bindCamera(item: DataModel.Camera) {
            val img:ImageView=itemView.findViewById(R.id.image)
            val txt:TextView=itemView.findViewById(R.id.text)
            //Do your view assignment here from the data model
            Glide.with(itemView.context).load(item.cameraImage).into(img)
            txt.setText("${adapterData.size-1}/${MY_PHOTO_NUMBER}")
            itemView.isEnabled = item.isEnabled

        }
        private fun bindImage(item: DataModel.Image) {
            Glide.with(itemView.context).load(item.image).into(itemView.findViewById(R.id.image) as ImageView)
        }
        fun bind(dataModel: DataModel) {
            when (dataModel) {
                is DataModel.Camera -> {bindCamera(dataModel) }
                is DataModel.Image -> bindImage(dataModel)

            }
        }
    }
    private var itemReviewAddClick: (() -> Unit)? = null
    fun setReviewAddClickListener(listener: (() -> Unit)) {
        itemReviewAddClick = listener
    }
    private var itemReviewDeleteClick: ((DataModel.Image,Int) -> Unit)? = null
    fun setReviewDeleteClickListener(listener: ((DataModel.Image,Int) -> Unit)) {
        itemReviewDeleteClick = listener
    }

    fun remove(it: DataModel.Image,position: Int) {
        adapterData.remove(it)
    }
}

sealed class DataModel {
    data class Camera(
        val cameraImage: Int,
        var isEnabled:Boolean
    ) : DataModel()
    data class Image(
        val id:Int,
        val image: String,
    ) : DataModel()
}