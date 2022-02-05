package com.ayata.clad.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.databinding.ItemRecyclerColorChooseBinding
import com.ayata.clad.databinding.RecyclerSearchRecentBinding

class AdapterRecentSearch(var context: Context,
                          var listItem: List<String>, var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdapterRecentSearch.ViewHolder>() {

    inner class ViewHolder(val binding: RecyclerSearchRecentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerSearchRecentBinding.
        inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(listItem[position]){
                binding.textView.text=listItem[position]
                binding.root.setOnClickListener {
                    onItemClickListener.onRecentSearchClicked(listItem[position],position)
                }
                binding.btnClose.setOnClickListener {
                    onItemClickListener.onCloseClicked(listItem[position],position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    interface OnItemClickListener{
        fun onCloseClicked(data:String,position:Int)
        fun onRecentSearchClicked(data:String,position:Int)
    }
}