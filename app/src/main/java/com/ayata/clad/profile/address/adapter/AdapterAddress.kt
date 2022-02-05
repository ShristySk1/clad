package com.ayata.clad.profile.address.adapter

import android.content.Context
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.shop.model.ModelShop
import com.ayata.clad.shopping_bag.model.ModelCheckout
import com.ayata.clad.shopping_bag.model.ModelShippingAddress
import com.ayata.clad.thrift.model.ModelThrift
import com.ayata.clad.utils.TextFormatter
import com.bumptech.glide.Glide
import org.w3c.dom.Text
import java.util.*

internal class AdapterAddress(private var context: Context?, private var listItems:List<ModelShippingAddress>,
                              private val onItemClickListener: OnItemClickListener
)
    : RecyclerView.Adapter<AdapterAddress.MyViewHolder>(){


        internal inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val name=itemView.findViewById<TextView>(R.id.title_address)
            val address=itemView.findViewById<TextView>(R.id.address)
            val editBtn=itemView.findViewById<ImageView>(R.id.editBtn)
            val checkBox=itemView.findViewById<CheckBox>(R.id.checkBox)
            val layoutMain=itemView.findViewById<View>(R.id.layout_main)

            fun clickView(){
               editBtn.setOnClickListener {
                   onItemClickListener.onEditClicked(listItems[adapterPosition],adapterPosition)
               }
                itemView.setOnClickListener {
                    onItemClickListener.onEditClicked(listItems[adapterPosition],adapterPosition)
                }
//                checkBox.setOnClickListener {
//                    onItemClickListener.onAddressSelected(listItems[adapterPosition],adapterPosition)
//                }

            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.recycler_shipping_address,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=listItems[position]

        holder.name.text=item.name
        holder.address.text=item.address
        holder.checkBox.visibility=View.GONE
//        holder.checkBox.isChecked=item.isSelected

//        if(item.isSelected){
//            holder.layoutMain.background=ContextCompat.getDrawable(context!!,R.drawable.background_outline_black)
//        }else{
//            holder.layoutMain.background=ContextCompat.getDrawable(context!!,R.drawable.background_outline_gray)
//        }

        holder.clickView()

    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    interface OnItemClickListener{
//        fun onAddressSelected(data:ModelShippingAddress,position:Int)
        fun onEditClicked(data:ModelShippingAddress,position:Int)
    }

}