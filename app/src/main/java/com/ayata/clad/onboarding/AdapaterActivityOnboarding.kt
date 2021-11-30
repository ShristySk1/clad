package com.ayata.clad.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R

internal class AdapaterActivityOnboarding(private var listner: AdapaterActivityOnboarding.setOnItemClickListener) :
    RecyclerView.Adapter<AdapaterActivityOnboarding.myViewHolder>() {

    internal inner class myViewHolder(myitems: View) : RecyclerView.ViewHolder(myitems) {
        fun clickView() {}
        var tvtitle: TextView
        init {
            tvtitle = myitems.findViewById(R.id.tv_heading)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewHolder {
        return myViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding, parent, false)
        )
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        if (position == 0) {
            holder.tvtitle.text = holder.tvtitle.context.getString(R.string.str_onheading)
        }
        if (position == 1) {
            holder.tvtitle.text = "welome to clad 2"
        }
        holder.clickView()
    }

    override fun getItemCount(): Int {
        return 3;
    }

    interface setOnItemClickListener {
        fun onItemClick(position: Int)
    }

}