package com.ayata.clad.view_all.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.home.model.ModelPopularBrands
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.view_all.model.ModelViewAllProduct
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

internal class AdapterViewAllBrand(var context: Context?,
                                   var itemList: List<ModelPopularBrands?>?,
                                   var onItemClickListener: OnItemClickListener):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.recycler_view_all_brand, parent, false)
            ItemViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.recycler_item_progress, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ItemViewHolder) {
            populateItemRows(viewHolder, position)
        } else if (viewHolder is LoadingViewHolder) {
            showLoadingView(viewHolder, position)
        }
    }

    override fun getItemCount(): Int {
        return if (itemList == null) 0 else itemList!!.size
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        return if (itemList!![position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }


    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textName: TextView
        var image: ImageView
        var progressBar:ProgressBar

        init {
            textName = itemView.findViewById(R.id.name)
//            textPrice = itemView.findViewById(R.id.price)
            image = itemView.findViewById(R.id.image)
            progressBar=itemView.findViewById(R.id.progressBar)
        }

        fun clickView(){
            itemView.setOnClickListener {
                onItemClickListener.onBrandClickListener(itemList!![adapterPosition]!!)
            }
        }
    }

    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById(R.id.progressBar)
        }
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //ProgressBar would be displayed
    }

    private fun populateItemRows(viewHolder: ItemViewHolder, position: Int) {
        val item = itemList!![position]
        viewHolder.textName.text = item!!.title

        viewHolder.progressBar.visibility = View.VISIBLE
        Glide.with(context!!)
            .load(item.imageDrawable)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    @Nullable e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    viewHolder.progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    viewHolder.progressBar.visibility = View.GONE
                    return false
                }
            })
            .error(R.drawable.shoes)
            .into(viewHolder.image)

        viewHolder.clickView()
    }

    interface OnItemClickListener{
        fun onBrandClickListener(data:ModelPopularBrands)
    }
}