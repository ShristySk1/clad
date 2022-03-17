package com.ayata.clad.shop.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.shop.model.ModelShop
import com.ayata.clad.shop.response.ChildCategory
import com.ayata.clad.utils.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.util.*

internal class AdapterSubCategoryFilterable(private var context: Context?, private var listItems:List<ChildCategory>,
                                            private val onSearchClickListener: OnSearchClickListener
)
    : RecyclerView.Adapter<AdapterSubCategoryFilterable.MyViewHolder>(),Filterable {

    var filterList: ArrayList<ChildCategory>

    init {
        filterList = listItems as ArrayList<ChildCategory>
    }

    fun setData(list:List<ModelShop>){
        filterList= list as ArrayList<ChildCategory>
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
        holder.title.text=item.title
        holder.progressBar.visibility = View.VISIBLE
        Log.d("myimage", "onBindViewHolder: "+item.image);
        try {
            Glide.with(context!!)
                .load(item.image)
                .fallback(Constants.ERROR_DRAWABLE)
                .error(Constants.ERROR_DRAWABLE)
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
                .into(holder.image)
        }catch (e:Exception){
            Glide.with(context!!)
                .load(Constants.ERROR_DRAWABLE)
                .into(holder.image)
            holder.progressBar.visibility = View.GONE
        }

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
                    listItems as ArrayList<ChildCategory>
                } else {
                    val resultList = ArrayList<ChildCategory>()
                    for (row in listItems) {
                        if (row.title!!.contains(charSearch,true)) {
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
                filterList = results?.values as ArrayList<ChildCategory>
                notifyDataSetChanged()
            }

        }
    }

    interface OnSearchClickListener{
        fun onSearchClick(data:ChildCategory)
    }

}