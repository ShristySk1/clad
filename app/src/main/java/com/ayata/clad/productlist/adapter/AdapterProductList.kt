package com.ayata.clad.productlist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.databinding.ItemRecyclerProductlistBinding
import com.ayata.clad.product.ModelProduct
import com.ayata.clad.utils.PreferenceHandler
import com.bumptech.glide.Glide
import java.util.ArrayList

class AdapterProductList(var context: Context?,
    var productList: List<ModelProduct>,
) : RecyclerView.Adapter<AdapterProductList.ViewHolder>(),Filterable {

    var filterList: ArrayList<ModelProduct>

    init {
        filterList = productList as ArrayList<ModelProduct>
    }

    fun setData(list:List<ModelProduct>){
        filterList= list as ArrayList<ModelProduct>
    }


    inner class ViewHolder(val binding: ItemRecyclerProductlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun clickView(item: ModelProduct) {
            itemView.setOnClickListener {
                itemProductClick?.let { function ->
                    function(item)
                }
            }
        }

        fun setValues(item: ModelProduct){
            binding.company.text=item.company
            Glide.with(context!!).asBitmap().load(item.image)
                .error(R.drawable.shoes)
                .placeholder(R.drawable.shoes)
                .into(binding.image)
            binding.name.text=item.name
            if(PreferenceHandler.getCurrency(context).equals(context!!.getString(R.string.npr_case),true)){
                binding.price.text="${context!!.getString(R.string.rs)} ${item.priceNPR}"
            }else{
                binding.price.text="${context!!.getString(R.string.usd)} ${item.priceUSD}"
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerProductlistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    // bind the items with each item
    // of the list languageList
    // which than will be
    // shown in recycler view
    // to keep it simple we are
    // not setting any image data to view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setValues(filterList[position])
        holder.clickView(filterList[position])
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return filterList.size
    }

    private var itemProductClick: ((ModelProduct) -> Unit)? = null
    fun setProductClickListener(listener: ((ModelProduct) -> Unit)) {
        itemProductClick = listener
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filterList = if (charSearch.isEmpty()) {
                    productList as ArrayList<ModelProduct>
                } else {
                    val resultList = ArrayList<ModelProduct>()
                    for (row in productList) {
                        if (row.name.contains(charSearch,true)) {
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
                filterList = results?.values as ArrayList<ModelProduct>
                notifyDataSetChanged()
            }

        }
    }
}