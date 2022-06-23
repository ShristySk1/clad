package com.ayata.clad.product.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.Nullable
import com.ayata.clad.R
import com.ayata.clad.home.response.Slider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.smarteist.autoimageslider.SliderViewAdapter


internal class AdapterProduct(private val context:Context, private val listItems: List<String>,
                              private val onItemClickListener: OnItemClickListener) : SliderViewAdapter<AdapterProduct.MyViewHolder>() {


    internal inner class MyViewHolder(itemView: View) : ViewHolder(itemView) {
//        var itemView: View
        var imageView: ImageView
        var progressBar: ProgressBar

        init {
            imageView = itemView.findViewById(R.id.image)
            progressBar = itemView.findViewById(R.id.progressBar)

//            this.itemView = itemView
        }

        fun clickView(data: String){
            itemView.setOnClickListener {
                onItemClickListener.onProductClicked(data)
            }
        }
    }

    override fun getCount(): Int {
        return listItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): MyViewHolder {
        val inflate: View =
            LayoutInflater.from(context).inflate(R.layout.layout_home_banner, null)
        return MyViewHolder(inflate)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder?, position: Int) {
        val item=listItems[position]
        Glide.with(context)
            .load(item)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    @Nullable e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    viewHolder!!.progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    viewHolder!!.progressBar.visibility = View.GONE
                    return false
                }
            })
            .error(R.drawable.ic_clad_logo_grey)
            .into(viewHolder!!.imageView)
        viewHolder.clickView(listItems[position])
    }

    interface OnItemClickListener{
        fun onProductClicked(data:String)
    }

}