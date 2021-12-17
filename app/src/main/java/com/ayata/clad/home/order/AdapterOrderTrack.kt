package com.ayata.clad.home.order

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.github.vipulasri.timelineview.TimelineView

class AdapterOrderTrack(private val context: Context, listitem: List<ModelOrderTrack>) :
    RecyclerView.Adapter<AdapterOrderTrack.modelViewHolder>() {
    private val listitem: List<ModelOrderTrack>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): modelViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_recycler_order_tracker, parent, false)
        return modelViewHolder(view, viewType)
    }
    override fun onBindViewHolder(holder: modelViewHolder, position: Int) {
        val modelOrderTrack: ModelOrderTrack = listitem[position]
        holder.title.setText(modelOrderTrack.orderTrackTitle)
        holder.desc.setText(modelOrderTrack.orderTrackDescription)
        val color: Int = listitem[position].color
        holder.mTimelineView.marker=context.getDrawable(R.drawable.circle_without_stroke)
        if (modelOrderTrack.ordertype.equals(ModelOrderTrack.ORDER_TYPE_NONE)) {
            holder.mTimelineView.marker=context.getDrawable(R.drawable.circle_without_stroke)
            //to set color for completed task
            holder.title.setTextColor(context.resources.getColor(color))
            holder.desc.setTextColor(context.resources.getColor(color))
        } else {
            holder.title.setTextColor(context.resources.getColor(color))
            holder.desc.setTextColor(context.resources.getColor(color))
            if (position == 0) {
                if (!modelOrderTrack.actual) {
                    holder.mTimelineView.setEndLineColor(context.resources.getColor(color), 1)
                }else{
                    holder.mTimelineView.marker=context.getDrawable(R.drawable.circle_with_stroke)
                }
                //do nothing
            } else {
                if (!listitem[position].actual) {
                    //already completed
                    holder.mTimelineView.setStartLineColor(context.resources.getColor(color), 0)
                    holder.mTimelineView.setEndLineColor(context.resources.getColor(color), 0)
                } else {
                    Log.d(
                        "currect",
                        "onBindViewHolder: position: " + position + "size" + listitem.size
                    )
                    //current
                    holder.mTimelineView.setStartLineColor(
                        context.resources.getColor(listitem[position - 1].color),
                        0
                    )

                    holder.mTimelineView.marker=context.getDrawable(R.drawable.circle_with_stroke)
                }
            }
            Log.d(
                "sizenadposition",
                "onBindViewHolder: position: " + position + "size" + listitem.size
            )
        }
    }

    override fun getItemCount(): Int {
        return listitem.size
    }

    inner class modelViewHolder(itemView: View, viewType: Int) :
        RecyclerView.ViewHolder(itemView) {
        var mTimelineView: TimelineView
        var title: TextView
        var desc: TextView
        init {
            mTimelineView = itemView.findViewById<View>(R.id.timeline) as TimelineView
            title = itemView.findViewById(R.id.title)
            desc = itemView.findViewById(R.id.desc)
            mTimelineView.initLine(viewType)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }
    init {
        this.listitem = listitem
    }
}
