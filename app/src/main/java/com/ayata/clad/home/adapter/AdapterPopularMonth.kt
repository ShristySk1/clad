package com.ayata.clad.home.adapter

import android.content.Context
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.home.model.ModelPopularMonth
import com.ayata.clad.shop.model.ModelShop
import com.ayata.clad.shopping_bag.model.ModelCheckout
import com.ayata.clad.shopping_bag.model.ModelPaymentMethod
import com.ayata.clad.shopping_bag.model.ModelShippingAddress
import com.ayata.clad.thrift.model.ModelThrift
import com.ayata.clad.utils.TextFormatter
import com.bumptech.glide.Glide
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

internal class AdapterPopularMonth(private var context: Context?, private var listItems:List<ModelPopularMonth>,
                                   private val onItemClickListener: OnItemClickListener
)
    : RecyclerView.Adapter<AdapterPopularMonth.MyViewHolder>(){


        internal inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val name=itemView.findViewById<TextView>(R.id.name)
            val price=itemView.findViewById<TextView>(R.id.price)
            val image=itemView.findViewById<ImageView>(R.id.image)
            val cardView=itemView.findViewById<CardView>(R.id.cardView)

            fun clickView(){
                cardView.setOnClickListener {
                    onItemClickListener.onPopularMonthClicked(listItems[adapterPosition],adapterPosition)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.recycler_home_popular_this_month,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=listItems[position]

        holder.name.text=item.title
        holder.price.text=item.price

        Glide.with(context!!).load(item.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.image)

        holder.clickView()

    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    interface OnItemClickListener{
        fun onPopularMonthClicked(data:ModelPopularMonth,position:Int)
    }

}