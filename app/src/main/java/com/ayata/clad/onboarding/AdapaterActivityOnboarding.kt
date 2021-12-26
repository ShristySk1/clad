package com.ayata.clad.onboarding

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.utils.PercentageCropImageView
import com.ayata.clad.utils.TopRightCropTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions



internal class AdapaterActivityOnboarding(private var listner: AdapaterActivityOnboarding.setOnItemClickListener) :
    RecyclerView.Adapter<AdapaterActivityOnboarding.myViewHolder>() {

    internal inner class myViewHolder(myitems: View) : RecyclerView.ViewHolder(myitems) {
        fun clickView() {}
        var tvtitle: TextView
        var image:PercentageCropImageView
        init {
            tvtitle = myitems.findViewById(R.id.tv_heading)
            image = myitems.findViewById(R.id.iv_onboardimage1)
            image.setCropYCenterOffsetPct(0f);
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
//        Glide.with(holder.itemView.context)
//            .load(R.drawable.splashimage)
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .transform(TopRightCropTransformation(holder.itemView.context, 0f, 0f))
//            .into(holder.image)

        if (position == 0) {
            holder.tvtitle.text = holder.tvtitle.context.getString(R.string.str_onheading)
        }
        if (position == 1) {
            holder.tvtitle.text = "Welome to Clad"
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