package com.ayata.clad.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ayata.clad.R
import com.ayata.clad.home.response.Sliders
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter


internal class AdapterBanner(private val context:Context,private val listItems:ArrayList<Sliders>,
    private val onItemClickListener: OnItemClickListener) : SliderViewAdapter<AdapterBanner.MyViewHolder>() {


    internal inner class MyViewHolder(itemView: View) : ViewHolder(itemView) {
//        var itemView: View
        var imageView: ImageView

        init {
            imageView = itemView.findViewById(R.id.image)
//            this.itemView = itemView
        }

        fun clickView(data: Sliders){
            itemView.setOnClickListener {
                onItemClickListener.onBannerClicked(data)
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
            .load(item.image_url)
            .fitCenter()
            .error(R.drawable.ic_clad_logo_grey)
            .into(viewHolder!!.imageView)
        viewHolder.clickView(listItems[position])
    }

    interface OnItemClickListener{
        fun onBannerClicked(data:Sliders)
    }

}