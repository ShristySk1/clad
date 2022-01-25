package com.ayata.clad.shopping_bag.adapter

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

internal class AdapterCircleText(private var context: Context?, private var listItems:List<ModelCircleText>)
    : RecyclerView.Adapter<AdapterCircleText.MyViewHolder>(){


        internal inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val textView=itemView.findViewById<TextView>(R.id.textView)
            val cardView=itemView.findViewById<CircularRevealCardView>(R.id.cardViewCircle)

            fun clickView(item:ModelCircleText){
               itemView.setOnClickListener {
                   itemCircleClick?.let { function ->
                       function(item)
                   }
               }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.recycler_size_circle,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=listItems[position]


        holder.textView.text=item.title
        if(item.isSelected) {
            holder.cardView.strokeColor = ContextCompat.getColor(context!!, R.color.colorBlack)
        }else{
            holder.cardView.strokeColor = ContextCompat.getColor(context!!, R.color.colorCircleUnselect)
        }

        holder.clickView(item)

    }


    override fun getItemCount(): Int {
        return listItems.size
    }

    private var itemCircleClick: ((ModelCircleText) -> Unit)? = null
    fun setCircleClickListener(listener: ((ModelCircleText) -> Unit)) {
        itemCircleClick = listener
    }

}