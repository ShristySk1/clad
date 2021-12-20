package com.ayata.clad.shopping_bag.adapter

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
import com.ayata.clad.shopping_bag.model.ModelPaymentMethod
import com.ayata.clad.shopping_bag.model.ModelShippingAddress
import com.ayata.clad.thrift.model.ModelThrift
import com.ayata.clad.utils.TextFormatter
import com.bumptech.glide.Glide
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

internal class AdapterPaymentMethod(private var context: Context?, private var listItems:List<ModelPaymentMethod>,
                                    private val onItemClickListener: OnItemClickListener
)
    : RecyclerView.Adapter<AdapterPaymentMethod.MyViewHolder>(){


        internal inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val name=itemView.findViewById<TextView>(R.id.textView3)
            val image=itemView.findViewById<ImageView>(R.id.imageView)
            val checkBox=itemView.findViewById<CheckBox>(R.id.checkBox)
            val layoutMain=itemView.findViewById<View>(R.id.layout_main)

            fun clickView(){
                itemView.setOnClickListener {
                    onItemClickListener.onPaymentMethodClicked(listItems[adapterPosition],adapterPosition)
                }
                checkBox.setOnClickListener {
                    onItemClickListener.onPaymentMethodClicked(listItems[adapterPosition],adapterPosition)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.item_recycler_payment,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=listItems[position]

        holder.name.text=item.name
        holder.checkBox.isChecked=item.isSelected

        val imageList= arrayListOf<String>(
            "https://play-lh.googleusercontent.com/Xh_OlrdkF1UnGCnMN__4z-yXffBAEl0eUDeVDPr4UthOERV4Fll9S-TozSfnlXDFzw",
            "data:image/png;base64,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",
            "https://www.imepay.com.np/wp-content/uploads/2020/10/IME-Pay-Icon.png"
        )

        if(item.isSelected){
            holder.layoutMain.background=ContextCompat.getDrawable(context!!,R.drawable.background_outline_black)
        }else{
            holder.layoutMain.background=ContextCompat.getDrawable(context!!,R.drawable.background_outline_gray)
        }

        Glide.with(context!!).load(imageList[position])
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.image)

        holder.clickView()

    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    interface OnItemClickListener{
        fun onPaymentMethodClicked(data:ModelPaymentMethod,position:Int)
    }

}