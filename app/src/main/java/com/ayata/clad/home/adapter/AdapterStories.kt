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
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.home.model.ModelStory
import com.ayata.clad.home.response.Story
import com.ayata.clad.utils.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

internal  class AdapterStories(private var context:Context?, private var listItems:List<Story>
    ,private var onItemClickListener: OnItemClickListener):RecyclerView.Adapter<AdapterStories.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.recycler_home_story,parent,false)
        return MyViewHolder(view)
    }
   internal inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

       val image=itemView.findViewById<ImageView>(R.id.imageView)
       val title=itemView.findViewById<TextView>(R.id.title)
       val progressBar=itemView.findViewById<ProgressBar>(R.id.progressBar)
       val cardViewImage=itemView.findViewById<CardView>(R.id.cardView_image)
       fun clickView(){
           cardViewImage.setOnClickListener {
               onItemClickListener.onStoryClick(listItems[adapterPosition],adapterPosition)
           }
       }


   }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=listItems[position]
        holder.title.text=item.vendor
        holder.progressBar.visibility = View.VISIBLE
        Glide.with(context!!).asDrawable().load(item.contents[0].imageUrl)
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
            .error(Constants.ERROR_DRAWABLE).into(holder.image)

        holder.clickView()
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    interface OnItemClickListener{
        fun onStoryClick(data: Story,position: Int)
    }

}