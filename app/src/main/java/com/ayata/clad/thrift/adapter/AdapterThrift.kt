package com.ayata.clad.thrift.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.shop.model.ModelShop
import com.ayata.clad.thrift.model.ModelThrift
import com.ayata.clad.utils.TextFormatter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import org.w3c.dom.Text
import java.util.*

internal class AdapterThrift(private var context: Context?, private var listItems:List<ModelThrift>,
                             private val onThriftClickListener: OnThriftClickListener
)
    : RecyclerView.Adapter<AdapterThrift.MyViewHolder>(){


        internal inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val title=itemView.findViewById<TextView>(R.id.name)
            val timeText=itemView.findViewById<TextView>(R.id.comment)
            val description=itemView.findViewById<TextView>(R.id.text_description)
            val imageLogo=itemView.findViewById<ImageView>(R.id.image_brand)
            val imageMain=itemView.findViewById<ImageView>(R.id.image_item)
            val layoutLike=itemView.findViewById<View>(R.id.cardView_like)
            val layoutComment=itemView.findViewById<View>(R.id.layout_comment)
            val imageShare=itemView.findViewById<ImageView>(R.id.image_share)
            val imageBookmark=itemView.findViewById<ImageView>(R.id.image_bookmark)
            val imageMore=itemView.findViewById<ImageView>(R.id.moreButton)
            val imageLike=itemView.findViewById<ImageView>(R.id.image_like)
            val textLike=itemView.findViewById<TextView>(R.id.text_like)
            val textComment=itemView.findViewById<TextView>(R.id.text_comment)
            val progressBar=itemView.findViewById<ProgressBar>(R.id.progressBar)

            fun clickView(){
               imageLogo.setOnClickListener {
                   onThriftClickListener.onLogoClicked(listItems[adapterPosition])
               }
                title.setOnClickListener {
                    onThriftClickListener.onLogoClicked(listItems[adapterPosition])
                }
                imageBookmark.setOnClickListener {
                    var isBookmark = listItems[adapterPosition].isBookmarked
                    isBookmark=if(isBookmark){
                        Glide.with(context!!).load(R.drawable.ic_bookmark_outline).into(imageBookmark)
                        false
                    }else{
                        Glide.with(context!!).load(R.drawable.ic_bookmark_filled).into(imageBookmark)
                        true
                    }
                    listItems[adapterPosition].isBookmarked=isBookmark
                    onThriftClickListener.onBookmarkClicked(listItems[adapterPosition],isBookmark)
                }
                layoutLike.setOnClickListener {
                    var isLike = listItems[adapterPosition].isLiked
                    isLike = if(isLike){
                        imageLike.setImageResource(R.drawable.ic_heart_outline)
                        imageLike.drawable.setTint(ContextCompat.getColor(context!!,R.color.colorBlack))
                        false
                    }else{
                        Glide.with(context!!).load(R.drawable.ic_heart_filled).into(imageLike)
                        true
                    }
                    listItems[adapterPosition].isLiked=isLike
                    onThriftClickListener.onLikeClicked(listItems[adapterPosition],isLike)
                }
                layoutComment.setOnClickListener {
                    onThriftClickListener.onCommentClicked(listItems[adapterPosition])
                }
                imageShare.setOnClickListener {
                    onThriftClickListener.onSendClicked(listItems[adapterPosition])
                }
                imageMore.setOnClickListener {
                    onThriftClickListener.onMoreClicked(listItems[adapterPosition])
                }


            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.recycler_thrift,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=listItems[position]

        val imageList= arrayListOf<String>("https://img.freepik.com/free-photo/joyful-young-brunette-with-fluffy-hair-red-lips-trendy-dress-black-jacket-belt-waist-standing-profile-sunny-street-smiling-against-light-building-wall_197531-24456.jpg?size=626&ext=jpg",
        "https://img.freepik.com/free-photo/fashionable-pale-brunette-long-green-dress-black-jacket-sunglasses-standing-street-during-daytime-against-wall-light-city-building_197531-24468.jpg?size=626&ext=jpg",
        "https://us.123rf.com/450wm/dmitrytsvetkov/dmitrytsvetkov1604/dmitrytsvetkov160400114/55316404-young-beautiful-pretty-girl-walking-along-the-street-with-handbag-and-cup-of-coffee-.jpg?ver=6")

        holder.timeText.text=item.timeText
        holder.title.text=item.name

        if(item.timeText.isNullOrBlank() || item.timeText.isNullOrEmpty()){
            holder.timeText.visibility=View.GONE
        }else{
            holder.timeText.visibility=View.VISIBLE
        }
        Glide.with(context!!).asDrawable()
            .load("https://i.pinimg.com/originals/e7/16/9c/e7169c1ffa2da5bfece0ad37c6c850e6.png")
            .error(R.drawable.logo_brand_example)
            .into(holder.imageLogo)

        holder.progressBar.visibility=View.VISIBLE
        Glide.with(context!!).asDrawable()
            .load(imageList.random())
            .listener(object :RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility=View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility=View.GONE
                    return false
                }

            })
//            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.background_gray)
            .into(holder.imageMain)

        seeMoreText(holder.description,item.description,item.boldText)

        holder.textLike.text=TextFormatter().numberFormatter(item.like.toLong())
        holder.textComment.text=TextFormatter().numberFormatter(item.comment.toLong())


        if(item.isBookmarked){
            Glide.with(context!!).load(R.drawable.ic_bookmark_filled).into(holder.imageBookmark)
        }else{
            Glide.with(context!!).load(R.drawable.ic_bookmark_outline).into(holder.imageBookmark)
        }

        if(item.isLiked){
            Glide.with(context!!).load(R.drawable.ic_heart_filled).into(holder.imageLike)
        }else{
//            Glide.with(context!!).asDrawable().load(R.drawable.ic_heart_outline).into(holder.imageLike)
            holder.imageLike.setImageResource(R.drawable.ic_heart_outline)
            holder.imageLike.drawable.setTint(ContextCompat.getColor(context!!,R.color.colorBlack))
        }

        holder.clickView()

    }

    private fun seeMoreText(description:TextView,textActual:String,boldText: String){
//        Html.fromHtml(textActual)
        description.text =SpannableStringBuilder().bold { append(boldText) }.append(" ").append(textActual)
        val moreBtnText="more"

        if (textActual.count()>90 || description.lineCount>2) {
            val text=textActual.substring(0,90)+"..."
            description.text =
                SpannableStringBuilder().bold { append(boldText) }.append(" ").append(text)
                    .color(ContextCompat.getColor(context!!,R.color.colorGray)){append(moreBtnText)}

            description.setOnClickListener {
                if(description.text.endsWith("..more",false)){
                    description.text =
                        SpannableStringBuilder().bold { append(boldText) }.append(" ").append(textActual)
                }else{
                    description.text =
                        SpannableStringBuilder().bold { append(boldText) }.append(" ").append(text)
                            .color(ContextCompat.getColor(context!!,R.color.colorGray)){append(moreBtnText)}
                }
            }

        }else{
            description.text =
                SpannableStringBuilder().bold { append(boldText) }.append(" ").append(textActual)
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    interface OnThriftClickListener{
        fun onMoreClicked(data:ModelThrift)
        fun onLikeClicked(data:ModelThrift,isLike:Boolean)
        fun onCommentClicked(data:ModelThrift)
        fun onSendClicked(data:ModelThrift)
        fun onBookmarkClicked(data:ModelThrift,isBookMark:Boolean)
        fun onLogoClicked(data:ModelThrift)
    }

}