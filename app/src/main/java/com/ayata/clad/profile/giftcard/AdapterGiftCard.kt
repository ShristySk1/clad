package com.ayata.clad.profile.giftcard

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
import com.ayata.clad.profile.giftcard.response.Coupon
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mikhaellopez.circularimageview.CircularImageView

internal class AdapterGiftCard(private var context:Context?,
                               private var listItems:List<Coupon>,
                               private val onItemClickListener: OnItemClickListener)
    :RecyclerView.Adapter<AdapterGiftCard.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.recycler_gift_card,parent,false)
        return MyViewHolder(view)
    }

   internal inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

       val title=itemView.findViewById<TextView>(R.id.textName)
       val description=itemView.findViewById<TextView>(R.id.textDescription)
       val valid=itemView.findViewById<TextView>(R.id.textValid)
       val code=itemView.findViewById<TextView>(R.id.textCode)

       fun clickView(){
           itemView.setOnClickListener {
               onItemClickListener.onGiftCardClick(listItems[adapterPosition],adapterPosition)
           }
       }

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=listItems[position]
        holder.title.text=item.title
        holder.description.text=item.description
        holder.valid.text="Valid until "+item.validTo
        holder.code.text=item.code

        holder.clickView()
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    interface OnItemClickListener{
        fun onGiftCardClick(data: Coupon, position:Int)
    }

}