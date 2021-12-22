package com.ayata.clad.home.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.home.model.ModelPopularBrands
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mikhaellopez.circularimageview.CircularImageView

internal class AdapterPopularBrands(private var context:Context?,
                                     private var listItems:List<ModelPopularBrands>,
                                     private val onItemClickListener: OnItemClickListener)
    :RecyclerView.Adapter<AdapterPopularBrands.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.recycler_home_brands,parent,false)
        return MyViewHolder(view)
    }

   internal inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

       val image=itemView.findViewById<CircularImageView>(R.id.imageView)
       val title=itemView.findViewById<TextView>(R.id.title)
       val description=itemView.findViewById<TextView>(R.id.desp)
       val progressBar=itemView.findViewById<ProgressBar>(R.id.progressBar)
       val layoutImage=itemView.findViewById<View>(R.id.imageLayout)
       fun clickView(){
           layoutImage.setOnClickListener {
               onItemClickListener.onPopularBrandsClicked(listItems[adapterPosition],adapterPosition)
           }
       }

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=listItems[position]
        holder.title.text=item.title
        holder.description.text=item.description

        holder.progressBar.visibility = View.VISIBLE
        Glide.with(context!!)
            .load(item.imageurl)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    @Nullable e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false
                }
            })
            .error(R.drawable.logo_brand_example)
            .into(holder.image)

        holder.clickView()
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    interface OnItemClickListener{
        fun onPopularBrandsClicked(data: ModelPopularBrands, position:Int)
    }

}