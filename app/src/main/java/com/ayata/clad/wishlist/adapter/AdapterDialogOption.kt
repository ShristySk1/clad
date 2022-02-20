package com.ayata.clad.wishlist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.shopping_bag.model.ModelCircleText
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import java.util.*

internal class AdapterDialogOption(private var context: Context?, private var listItems:List<String>)
    : RecyclerView.Adapter<AdapterDialogOption.MyViewHolder>(){


        internal inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val textView=itemView.findViewById<TextView>(R.id.title)

            fun clickView(item:String){
               itemView.setOnClickListener {
                   itemOptionClick?.let { function ->
                       function(item)
                   }
               }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.dialog_recycler_wishlist,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=listItems[position]
        holder.textView.text=item

        holder.clickView(item)

    }


    override fun getItemCount(): Int {
        return listItems.size
    }

    private var itemOptionClick: ((String) -> Unit)? = null
    fun setOptionClickListener(listener: ((String) -> Unit)) {
        itemOptionClick = listener
    }

}