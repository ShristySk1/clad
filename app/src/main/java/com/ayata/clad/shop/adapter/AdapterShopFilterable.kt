package com.ayata.clad.shop.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.shop.model.ModelShop
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.util.*

internal class AdapterShopFilterable(private var context: Context?, private var listItems:List<ModelShop>,
                                     private val onSearchClickListener: OnSearchClickListener
)
    : RecyclerView.Adapter<AdapterShopFilterable.MyViewHolder>(),Filterable {

    var filterList: ArrayList<ModelShop>

    init {
        filterList = listItems as ArrayList<ModelShop>
    }

    fun setData(list:List<ModelShop>){
        filterList= list as ArrayList<ModelShop>
    }

        internal inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val title=itemView.findViewById<TextView>(R.id.name)
            val comment=itemView.findViewById<TextView>(R.id.comment)
            val image=itemView.findViewById<ImageView>(R.id.image)
            val progressBar=itemView.findViewById<ProgressBar>(R.id.progressBar)

            fun clickView(){
               itemView.setOnClickListener {
                   onSearchClickListener.onSearchClick(filterList[adapterPosition])
               }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.recycler_shop,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=filterList[position]

        holder.comment.text=item.comment
        holder.title.text=item.name
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
            .error(R.drawable.popularcard)
            .into(holder.image)
        if(item.comment.isNullOrBlank() || item.comment.isNullOrEmpty()){
            holder.comment.visibility=View.GONE
        }else{
            holder.comment.visibility=View.VISIBLE
        }
        holder.clickView()

    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filterList = if (charSearch.isEmpty()) {
                    listItems as ArrayList<ModelShop>
                } else {
                    val resultList = ArrayList<ModelShop>()
                    for (row in listItems) {
                        if (row.name!!.contains(charSearch,true)) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as ArrayList<ModelShop>
                notifyDataSetChanged()
            }

        }
    }

    interface OnSearchClickListener{
        fun onSearchClick(data:ModelShop)
    }

}