package com.ayata.clad.shopping_bag.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.shopping_bag.model.ModelCheckout
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.PreferenceHandler
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

internal class AdapterCheckout(
    private var context: Context?, private var listItems: MutableList<ModelCheckout>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdapterCheckout.MyViewHolder>() {


    private val STOCKLIMIT: Int = 6

    internal inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name = itemView.findViewById<TextView>(R.id.name)
        val price = itemView.findViewById<TextView>(R.id.price)
        val size = itemView.findViewById<TextView>(R.id.size)
        val quantity = itemView.findViewById<TextView>(R.id.quantity)
        val itemId = itemView.findViewById<TextView>(R.id.itemId)
        val image = itemView.findViewById<ImageView>(R.id.image)
        val layoutQuantity = itemView.findViewById<View>(R.id.layout_quantity)
        val layoutSize = itemView.findViewById<View>(R.id.layout_size)
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)
        val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
        val cardViewImage = itemView.findViewById<CardView>(R.id.cardView)
        val add = itemView.findViewById<ImageView>(R.id.ivAdd)
        val remove = itemView.findViewById<ImageView>(R.id.ivRemove)
        val number = itemView.findViewById<TextView>(R.id.number)
        val color = itemView.findViewById<TextView>(R.id.color)
        val colorHexImage = itemView.findViewById<ImageView>(R.id.ivColor)
        val brand = itemView.findViewById<TextView>(R.id.tvBrandName)
        val stock = itemView.findViewById<TextView>(R.id.stock)
        var coupen = itemView.findViewById<TextView>(R.id.tv_text_to_copy)


        fun clickView() {
            layoutQuantity.setOnClickListener {
                onItemClickListener.onQuantityClicked(listItems[adapterPosition], adapterPosition)
            }
            layoutSize.setOnClickListener {
                onItemClickListener.onSizeClicked(listItems[adapterPosition], adapterPosition)
            }
            checkBox.setOnClickListener {
                val isChecked = checkBox.isChecked
                onItemClickListener.onCheckBoxClicked(
                    listItems[adapterPosition],
                    isChecked,
                    adapterPosition
                )
            }
            cardViewImage.setOnClickListener {
                checkBox.toggle()
                val isChecked = checkBox.isChecked
                onItemClickListener.onCheckBoxClicked(
                    listItems[adapterPosition],
                    isChecked,
                    adapterPosition
                )
            }

            add.setOnClickListener {
                Log.d("tetstdata", "clickView: ");
                onItemClickListener.onAddClick(listItems[adapterPosition], adapterPosition)
            }
            remove.setOnClickListener {
                if (adapterPosition != -1) {
                    try {
                        if (listItems[adapterPosition].qty == 1) {
                            onItemClickListener.onCompleteRemove(
                                listItems[adapterPosition],
                                adapterPosition
                            )
                        } else {
                            onItemClickListener.onRemove(
                                listItems[adapterPosition],
                                adapterPosition
                            )
                        }
                    } catch (e: Exception) {
                        Log.d("testlog", "onBindViewHolder: " + e.message);
                    }

                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_checkout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = listItems[position]

        holder.name.text = item.name
        if (PreferenceHandler.getCurrency(context)
                .equals(context!!.getString(R.string.npr_case), true)
        ) {
            holder.price.text = "${context!!.getString(R.string.rs)} ${item.priceNPR}"
        } else {
            holder.price.text = "${context!!.getString(R.string.usd)} ${item.priceUSD}"
        }
        if (item.size.isNotEmpty()) {
            holder.size.text = "Size: " + item.size.toUpperCase()
            holder.layoutSize.visibility = View.VISIBLE
        } else {
            holder.layoutSize.visibility = View.GONE
        }
        holder.quantity.text = "QTY: " + item.qty
        holder.itemId.text = "Item ID: " + item.itemId
        holder.checkBox.isChecked = item.isSelected
        holder.number.text = (item.qty.toString())
        holder.color.text = item.color + ","
        holder.colorHexImage.apply { setColorFilter(Color.parseColor(item.colorHex)) }
        holder.brand.text = "Brand: ${item.brand}"
        holder.progressBar.visibility = View.VISIBLE
        Log.d("testimage", "onBindViewHolder: " + item.image);
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
            .error(Constants.ERROR_DRAWABLE)
            .fallback(Constants.ERROR_DRAWABLE)
            .into(holder.image)
        //stock calculate
//        val stock = item.stock
//        val myQuantity = item.qty
//        var textToDisplay = ""
//        if (myQuantity >= stock) {
//            textToDisplay = "Out of Stock"
//            changeColor(holder.stock, R.color.colorRedLight, R.color.colorRedDark)
//        } else if (myQuantity < stock) {
//            if ((stock - myQuantity) < STOCKLIMIT) {
//                changeColor(holder.stock, R.color.colorYellowLight, R.color.colorYellowDark)
//                textToDisplay = "${stock - myQuantity} item(s) remaining"
//            } else {
//                textToDisplay = "In stock"
//                changeColor(holder.stock, R.color.colorGreenLight, R.color.colorGreenDark)
//            }

//        }
        holder.stock.setText(setStockStatus(item.stock, holder.stock, holder.image.context))
        if (item.isCoupenAvailable) {
            holder.coupen.visibility = View.VISIBLE
            holder.coupen.text = item.coupenText
        } else {
            holder.coupen.visibility = View.GONE
        }
        //click
        holder.clickView()
//        holder.remove.setOnClickListener {
//            if(position!=-1) {
//                try {
//                    if (item.qty == 1) {
//                        onItemClickListener.onCompleteRemove(listItems[position], position)
//                    } else {
//                        onItemClickListener.onRemove(listItems[position], position)
//                    }
//                }catch (e:Exception){
//                    Log.d("testlog", "onBindViewHolder: "+e.message);
//                }
//
//            }
//
//        }


    }

    fun getList() = listItems

    private fun changeColor(stock: TextView, colorLight: Int, colorDark: Int) {
        stock.setTextColor(ContextCompat.getColor(context!!, colorDark));
        stock.background.setTint(ContextCompat.getColor(context!!, colorLight));
    }

    override fun getItemCount(): Int {
        Log.d("testsize", "getItemCount: " + listItems.size);
        return listItems.size
    }

    fun removeItem(position: Int): ModelCheckout? {
        var item: ModelCheckout? = null
        try {
            item = listItems.get(position)
            listItems.removeAt(position)
            notifyItemRemoved(position)
        } catch (e: Exception) {
            Log.e("test", e.message!!)
        }
        return item
    }

    fun addItem(item: ModelCheckout, position: Int) {
        try {
            listItems.add(position, item)
            notifyItemInserted(position)
        } catch (e: java.lang.Exception) {
            Log.e("MainActivity", e.message!!)
        }
    }

    fun setStockStatus(stock: String, tv_stock: TextView, context: Context): String {
        var textToDisplay = stock
        if (stock.contains("Out of Stock", ignoreCase = true)) {
            changeColor(tv_stock, R.color.colorRedLight, R.color.colorRedDark, context)
        } else if (stock.contains("In stock", ignoreCase = true)) {
            changeColor(tv_stock, R.color.colorGreenLight, R.color.colorGreenDark, context)

        } else {
            changeColor(tv_stock, R.color.colorYellowLight, R.color.colorYellowDark, context)
        }
        return textToDisplay
    }

    private fun changeColor(stock: TextView, colorLight: Int, colorDark: Int, context: Context) {
        stock.setTextColor(ContextCompat.getColor(context, colorDark));
        stock.background.setTint(ContextCompat.getColor(context, colorLight));
    }

    fun getCartId(position: Int): Int {
        return listItems.get(position).cartId
    }

    interface OnItemClickListener {
        fun onSizeClicked(data: ModelCheckout, position: Int)
        fun onQuantityClicked(data: ModelCheckout, position: Int)
        fun onCheckBoxClicked(data: ModelCheckout, isChecked: Boolean, position: Int)
        fun onAddClick(data: ModelCheckout, position: Int)
        fun onRemove(data: ModelCheckout, position: Int)
        fun onCompleteRemove(data: ModelCheckout, position: Int)
    }

}