package com.ayata.clad.productlist.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.databinding.RecyclerHomeJustDroppedBinding
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.utils.PreferenceHandler
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.util.*

class AdapterProductList(
    var context: Context?,
    var productList: List<ProductDetail>,
) : RecyclerView.Adapter<AdapterProductList.ViewHolder>(), Filterable {

    var filterList: ArrayList<ProductDetail>

    init {
        filterList = productList as ArrayList<ProductDetail>
    }

    fun setData(list: List<ProductDetail>) {
        filterList = list as ArrayList<ProductDetail>
    }


    inner class ViewHolder(val binding: RecyclerHomeJustDroppedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun clickView(item: ProductDetail) {
            itemView.setOnClickListener {
                itemProductClick?.let { function ->
                    function(item)
                }
            }
        }

        fun setValues(item: ProductDetail) {
            Glide.with(context!!).asBitmap().load(item.vendor)
                .error(R.drawable.ic_clad_logo_grey).into(binding.imageLogo)
            Glide.with(context!!)
                .load(item.imageUrl[0])
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        @Nullable e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }
                })
                .error(R.drawable.shoes)
                .into(binding.image)
            binding.name.text = item.name
            binding.name.setTextColor(ContextCompat.getColor(binding.name.context,R.color.black))

            if (PreferenceHandler.getCurrency(context)
                    .equals(context!!.getString(R.string.npr_case), true)
            ) {
                //nrp
                binding.price.text = "${context!!.getString(R.string.rs)} ${item.price}"
            } else {
                //usd
                binding.price.text = "${context!!.getString(R.string.usd)} ${item.dollar_price}"
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerHomeJustDroppedBinding.inflate(
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

    private var itemProductClick: ((ProductDetail) -> Unit)? = null
    fun setProductClickListener(listener: ((ProductDetail) -> Unit)) {
        itemProductClick = listener
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filterList = if (charSearch.isEmpty()) {
                    productList as ArrayList<ProductDetail>
                } else {
                    val resultList = ArrayList<ProductDetail>()
                    for (row in productList) {
                        if (row.name.contains(charSearch, true)) {
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
                filterList = results?.values as ArrayList<ProductDetail>
                notifyDataSetChanged()
            }

        }
    }
}