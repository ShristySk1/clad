package com.ayata.clad.product

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentProductDetailFull2Binding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.ybq.android.spinkit.SpinKitView


class FragmentProductDetailFull2 : Fragment() {
    var mUrls: ArrayList<String>? = null
    private lateinit var binding: FragmentProductDetailFull2Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailFull2Binding.inflate(inflater, container, false)
        initView()
        def()
        setup()
        return binding.root
    }

    private fun initView() {
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "",
            textDescription = ""
        )
        (activity as MainActivity).showBottomNavigation(false)
    }

    private fun getBundle(): ArrayList<String> {
        val bundle = arguments
        if (bundle != null) {
            val data = bundle.getSerializable("gallary") as ArrayList<String>
            if (data != null) {
//                Glide.with(requireContext()).load(data.image_url).into(binding.)
//                val dynamicMediaList: MutableList<MediaInfo> = ArrayList()
//                val images = arrayListOf<String>(
//                    "https://cdn.shopify.com/s/files/1/0010/6518/9429/products/IMG_2333_1000x1000.jpg?v=1522633014",
//                    "https://cdn.shopify.com/s/files/1/0010/6518/9429/products/IMG_2341_1000x1000.jpg?v=1522633015",
//                    "https://cdn.shopify.com/s/files/1/0010/6518/9429/products/IMG_2347_1000x1000.jpg?v=1522633016"
//                )
                return data

            }else{
               return ArrayList<String>()
            }

        }else{
            return  ArrayList<String>()
        }
    }

    private fun def() {
        mUrls = getBundle()
    }


    private fun getMockImgs(): ArrayList<String> {
        val tmp: ArrayList<String> = ArrayList()
//        tmp.add("https://cdn.shopify.com/s/files/1/0010/6518/9429/products/IMG_2333_1000x1000.jpg?v=1522633014")
//        tmp.add("https://cdn.shopify.com/s/files/1/0010/6518/9429/products/IMG_2341_1000x1000.jpg?v=1522633015")
//        tmp.add("https://cdn.shopify.com/s/files/1/0010/6518/9429/products/IMG_2347_1000x1000.jpg?v=1522633016")

        tmp.add("https://cdn.shopify.com/s/files/1/0010/6518/9429/products/2048x1367-49_1000x1000.jpg?v=1522214294")
        tmp.add("https://cdn.shopify.com/s/files/1/0010/6518/9429/products/2048x1367-50_1000x1000.jpg?v=1522214295")
        tmp.add("https://cdn.shopify.com/s/files/1/0010/6518/9429/products/2048x1367-51_2000x2000.jpg?v=1522214295")

        return tmp
    }

    var adp: VpAdapter? = null

    private fun setup() {
        adp = VpAdapter()
        binding.vpMain!!.adapter = adp
        binding.indicator!!.setupWithViewPager(binding.vpMain, mUrls as ArrayList<String?>?, 70f)
    }

    inner class VpAdapter : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val v: View = getLayoutInflater().inflate(R.layout.thumb_item, container, false)
            val imgSlider = v.findViewById<ImageView>(R.id.imgSlider)
            val progress=v.findViewById<SpinKitView>(R.id.defaultProgress)
            progress.visibility=View.VISIBLE
            Glide.with(container.context).load(mUrls?.get(position))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object :
                RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progress.visibility=View.GONE
                    return false
                }

            }).into(imgSlider)
            container.addView(v)
            return v
        }

        override fun getItemPosition(`object`: Any): Int {
            return if (mUrls?.indexOf(`object`) == -1) POSITION_NONE else super.getItemPosition(
                `object`
            )
        }

        override fun isViewFromObject(view: View, o: Any): Boolean {
            return view === o
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getCount(): Int {
            return mUrls!!.size
        }
    }

}