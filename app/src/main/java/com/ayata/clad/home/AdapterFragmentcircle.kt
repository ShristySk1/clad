package com.ayata.clad.home

import android.content.Context
import android.icu.text.LocaleDisplayNames
import android.media.Image
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView

internal  class AdapterFragmentcircle(private var context:Context?,private var listItems:List<Modelcircularlist>):RecyclerView.Adapter<AdapterFragmentcircle.MyViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterFragmentcircle.MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.items_recycler_story,parent,false)
        return MyViewHolder(view)
    }

   internal class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

       val circularimage=itemView.findViewById<CircularImageView>(R.id.circle_view)
       val texttitle=itemView.findViewById<TextView>(R.id.titilecircular);

    }

    override fun onBindViewHolder(holder: AdapterFragmentcircle.MyViewHolder, position: Int) {
        val item=listItems[position]
        holder.texttitle.text=item.title
        Glide.with(context!!).asBitmap().load(item.imageurl).into(holder.circularimage)
    }

    override fun getItemCount(): Int {

        return listItems.size;
    }


}