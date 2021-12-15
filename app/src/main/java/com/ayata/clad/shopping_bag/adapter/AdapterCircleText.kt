package com.ayata.clad.shopping_bag.adapter

import android.content.Context
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.shop.model.ModelShop
import com.ayata.clad.shopping_bag.model.ModelCircleText
import com.ayata.clad.thrift.model.ModelThrift
import com.ayata.clad.utils.TextFormatter
import com.bumptech.glide.Glide
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import org.w3c.dom.Text
import java.util.*

internal class AdapterCircleText(private var context: Context?, private var listItems:List<ModelCircleText>
)
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
            holder.cardView.strokeColor = ContextCompat.getColor(context!!, R.color.black)
        }else{
            holder.cardView.strokeColor = ContextCompat.getColor(context!!, R.color.colorCircleUnselect)
        }

        holder.clickView(item)

    }


    override fun getItemCount(): Int {
        return listItems.size
    }

//    interface OnCircleClickListener{
//        fun onCircleClicked(data:ModelCircleText)
//    }

    private var itemCircleClick: ((ModelCircleText) -> Unit)? = null
    fun setCircleClickListener(listener: ((ModelCircleText) -> Unit)) {
        itemCircleClick = listener
    }

}