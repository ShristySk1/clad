package com.ayata.clad.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.home.model.ModelStories
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView

internal  class AdapterStories(private var context:Context?, private var listItems:List<ModelStories>):RecyclerView.Adapter<AdapterStories.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.recycler_home_story,parent,false)
        return MyViewHolder(view)
    }

   internal inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

       val image=itemView.findViewById<CircularImageView>(R.id.imageView)
       val title=itemView.findViewById<TextView>(R.id.title)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=listItems[position]
        holder.title.text=item.title
        Glide.with(context!!).asBitmap().load(item.imageurl).into(holder.image)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }


}