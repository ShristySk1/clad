package com.ayata.clad.home.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.utils.PreferenceHandler
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.util.*

internal class AdapterPopularMonth(private var context: Context?, private var listItems:List<ProductDetail>,
                                   private val onItemClickListener: OnItemClickListener
)
    : RecyclerView.Adapter<AdapterPopularMonth.MyViewHolder>(){


        internal inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val name=itemView.findViewById<TextView>(R.id.name)
            val price=itemView.findViewById<TextView>(R.id.price)
            val image=itemView.findViewById<ImageView>(R.id.image)
//            val cardView=itemView.findViewById<CardView>(R.id.cardView)
            val progressBar=itemView.findViewById<ProgressBar>(R.id.progressBar)

            fun clickView(){
                itemView.setOnClickListener {
                    onItemClickListener.onPopularMonthClicked(listItems[adapterPosition],adapterPosition)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.recycler_home_recommended,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=listItems[position]

        holder.name.text=item.name

        if(PreferenceHandler.getCurrency(context).equals(context!!.getString(R.string.npr_case),true)){
            holder.price.text="${context!!.getString(R.string.rs)} ${item.price}"
        }else{
            holder.price.text="${context!!.getString(R.string.usd)} ${item.dollar_price}"
        }

        holder.progressBar.visibility = View.VISIBLE
        Glide.with(context!!).load(item.imageUrl[0])
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
//            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.popularcard)
            .into(holder.image)

        holder.clickView()

    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    interface OnItemClickListener{
        fun onPopularMonthClicked(data:ProductDetail,position:Int)
    }

}