package com.ayata.clad.home.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.home.model.ModelJustDropped
import com.ayata.clad.home.model.ModelMostPopular
import com.ayata.clad.home.model.ModelPopularBrands
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mikhaellopez.circularimageview.CircularImageView

internal class AdapterMostPopular(private var context:Context?,
                                  private var listItems:List<ModelMostPopular>,
                                  private val onItemClickListener: OnItemClickListener)
    :RecyclerView.Adapter<AdapterMostPopular.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.recycler_home_just_dropped,parent,false)
        return MyViewHolder(view)
    }

   internal inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

       val image=itemView.findViewById<ImageView>(R.id.image)
       val imageLogo=itemView.findViewById<ImageView>(R.id.image_logo)
       val title=itemView.findViewById<TextView>(R.id.name)
//       val description=itemView.findViewById<TextView>(R.id.ask_text)
       val price=itemView.findViewById<TextView>(R.id.price)

       val progressBar=itemView.findViewById<ProgressBar>(R.id.progressBar)
       fun clickView(){
           itemView.setOnClickListener {
               onItemClickListener.onMostPopularClicked(listItems[adapterPosition],adapterPosition)
           }
       }

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=listItems[position]
        holder.title.text=item.title
//        holder.description.text=item.description
        holder.price.text=item.price

        holder.progressBar.visibility = View.VISIBLE
        Glide.with(context!!)
            .load(item.imageUrl)
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
            .error(R.drawable.shoes)
            .into(holder.image)

        Glide.with(context!!).asBitmap().load(item.logoUrl)
            .error(R.drawable.ic_hanger).into(holder.imageLogo)

        holder.clickView()
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    interface OnItemClickListener{
        fun onMostPopularClicked(data: ModelMostPopular, position:Int)
    }

}