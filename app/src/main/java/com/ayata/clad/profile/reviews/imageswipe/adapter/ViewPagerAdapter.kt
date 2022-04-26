package com.ayata.clad.profile.reviews.imageswipe.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.ayata.clad.R
import com.ayata.clad.profile.reviews.adapter.DataModel
import com.bumptech.glide.Glide
import com.igreenwood.loupe.Loupe

class ViewPagerAdapter(// Context object
    var context: Context, // Array of images
    var images: List<String>
) :
    PagerAdapter() {
    // Layout Inflater
    var mLayoutInflater: LayoutInflater
    override fun getCount(): Int {
        // return the number of images
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // inflating the item.xml
        val itemView: View =
            mLayoutInflater.inflate(R.layout.item_recycler_review_image_full, container, false)
        // referencing the image view from the item.xml file
        val imageView = itemView.findViewById<ImageView>(R.id.imageViewMain)
        val top = itemView.findViewById<LinearLayout>(R.id.container)
        // setting the image in the imageView
        Log.d("printimages", "instantiateItem: " + images[position]);
        Glide.with(context).load(images[position]).into(imageView)
        val loupe = Loupe.create(imageView, top) { // imageView is your ImageView
            onViewTranslateListener = object : Loupe.OnViewTranslateListener {
                override fun onStart(view: ImageView) {
                    // called when the view starts moving
                }

                override fun onViewTranslate(view: ImageView, amount: Float) {
                    // called whenever the view position changed
                }

                override fun onRestore(view: ImageView) {
                    // called when the view drag gesture ended
                }

                override fun onDismiss(view: ImageView) {
                    // called when the view drag gesture ended
                    itemImageDragEnded?.let {
                        it()
                    }
                }
            }
        }
        container.addView(itemView);
        return itemView
    }
    private var itemImageDragEnded: (() -> Unit)? = null
    fun setImageDragListener(listener: (() -> Unit)) {
        itemImageDragEnded = listener
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

    // Viewpager Constructor
    init {
        images = images
        mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}