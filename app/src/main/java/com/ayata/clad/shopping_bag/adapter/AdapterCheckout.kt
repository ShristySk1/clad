package com.ayata.clad.shopping_bag.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.shop.model.ModelShop
import com.ayata.clad.shopping_bag.model.ModelCheckout
import com.ayata.clad.thrift.model.ModelThrift
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.TextFormatter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import org.w3c.dom.Text
import java.util.*

internal class AdapterCheckout(private var context: Context?, private var listItems:List<ModelCheckout>,
                               private val onItemClickListener: OnItemClickListener
)
    : RecyclerView.Adapter<AdapterCheckout.MyViewHolder>(){


        internal inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val name=itemView.findViewById<TextView>(R.id.name)
            val price=itemView.findViewById<TextView>(R.id.price)
            val size=itemView.findViewById<TextView>(R.id.size)
            val quantity=itemView.findViewById<TextView>(R.id.quantity)
            val itemId=itemView.findViewById<TextView>(R.id.itemId)
            val image=itemView.findViewById<ImageView>(R.id.image)
            val layoutQuantity=itemView.findViewById<View>(R.id.layout_quantity)
            val layoutSize=itemView.findViewById<View>(R.id.layout_size)
            val checkBox=itemView.findViewById<CheckBox>(R.id.checkBox)
            val progressBar=itemView.findViewById<ProgressBar>(R.id.progressBar)
            val cardViewImage=itemView.findViewById<CardView>(R.id.cardView)

            fun clickView(){
               layoutQuantity.setOnClickListener {
                   onItemClickListener.onQuantityClicked(listItems[adapterPosition],adapterPosition)
               }
                layoutSize.setOnClickListener {
                    onItemClickListener.onSizeClicked(listItems[adapterPosition],adapterPosition)
                }
                checkBox.setOnClickListener {
                    val isChecked=checkBox.isChecked
                    onItemClickListener.onCheckBoxClicked(listItems[adapterPosition],isChecked,adapterPosition)
                }
                cardViewImage.setOnClickListener{
                    checkBox.toggle()
                    val isChecked=checkBox.isChecked
                    onItemClickListener.onCheckBoxClicked(listItems[adapterPosition],isChecked,adapterPosition)
                }

            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.recycler_checkout,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=listItems[position]

        holder.name.text=item.name
        if(PreferenceHandler.getCurrency(context).equals(context!!.getString(R.string.npr_case),true)){
            holder.price.text="${context!!.getString(R.string.rs)} ${item.priceNPR}"
        }else{
            holder.price.text="${context!!.getString(R.string.usd)} ${item.priceUSD}"
        }

        holder.size.text="Size: "+item.size.toUpperCase()
        holder.quantity.text="QTY: "+item.qty
        holder.itemId.text="Item ID: "+item.itemId
        holder.checkBox.isChecked=item.isSelected

        holder.progressBar.visibility = View.VISIBLE
        Glide.with(context!!).asDrawable()
            .load(item.image)
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
            .error(R.drawable.shoes)
            .into(holder.image)

        holder.clickView()

    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    interface OnItemClickListener{
        fun onSizeClicked(data:ModelCheckout,position:Int)
        fun onQuantityClicked(data:ModelCheckout,position:Int)
        fun onCheckBoxClicked(data:ModelCheckout,isChecked:Boolean,position:Int)
    }

}