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
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.view_all.model.ModelViewAllProduct
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

internal class AdapterViewAllProduct(var context: Context?,
                                     var itemList: List<ModelViewAllProduct?>?,
                                     var onItemClickListener: OnItemClickListener):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.recycler_view_all_product, parent, false)
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
        var textPrice: TextView
        var textCompany: TextView
        var image: ImageView
        var progressBar:ProgressBar
        var imageWish:ImageView

        init {
            textName = itemView.findViewById(R.id.name)
            textPrice = itemView.findViewById(R.id.price)
            textCompany = itemView.findViewById(R.id.company)
            image = itemView.findViewById(R.id.image)
            progressBar=itemView.findViewById(R.id.progressBar)
            imageWish=itemView.findViewById(R.id.iconWish)
        }

        fun clickView(){
            itemView.setOnClickListener {
                onItemClickListener.onProductClickListener(itemList!![adapterPosition]!!)
            }

            imageWish.setOnClickListener {
                onItemClickListener.onWishListClicked(itemList!![adapterPosition]!!,adapterPosition)
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
        viewHolder.textCompany.text = item!!.company
        if(PreferenceHandler.getCurrency(context).equals(context!!.getString(R.string.npr_case),true)){
            viewHolder.textPrice.text="${context!!.getString(R.string.rs)} ${item.priceNPR}"
        }else{
            viewHolder.textPrice.text="${context!!.getString(R.string.usd)} ${item.priceUSD}"
        }

        viewHolder.progressBar.visibility = View.VISIBLE
        Glide.with(context!!)
            .load(item.imageUrl)
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

        if(item.isOnWishList){
            Glide.with(context!!).load(R.drawable.ic_heart_filled).into(viewHolder.imageWish)
        }else{
            Glide.with(context!!).load(R.drawable.ic_heart_outline).into(viewHolder.imageWish)
        }

        viewHolder.clickView()
    }

    interface OnItemClickListener{
        fun onProductClickListener(data:ModelViewAllProduct)
        fun onWishListClicked(data: ModelViewAllProduct,position: Int)
    }
}