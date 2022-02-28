package com.ayata.clad.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentProductDetailFullBinding
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.test2.PicassoImageLoader

import com.veinhorn.scrollgalleryview.MediaInfo
import com.veinhorn.scrollgalleryview.ScrollGalleryView
import com.veinhorn.scrollgalleryview.builder.GallerySettings
import java.util.*


class FragmentProductDetailFull : Fragment() {
    private lateinit var galleryView: ScrollGalleryView
    private lateinit var binding: FragmentProductDetailFullBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailFullBinding.inflate(inflater, container, false)
        initView()
        getBundle()
//        setUpFullScreen()


        return binding.root
    }

    private fun initView() {
        (activity as MainActivity).showToolbar(false)
        (activity as MainActivity).showBottomNavigation(false)
    }

    private fun getBundle() {
        val bundle = arguments
        if (bundle != null) {
            val data = bundle.getSerializable(FragmentHome.PRODUCT_DETAIL) as ProductDetail
            if (data != null) {
//                Glide.with(requireContext()).load(data.image_url).into(binding.)
                val dynamicMediaList: MutableList<MediaInfo> = ArrayList()
                val images = arrayListOf<String>(
                    "https://cdn.shopify.com/s/files/1/0010/6518/9429/products/IMG_2333_1000x1000.jpg?v=1522633014",
                    "https://cdn.shopify.com/s/files/1/0010/6518/9429/products/IMG_2341_1000x1000.jpg?v=1522633015",
                    "https://cdn.shopify.com/s/files/1/0010/6518/9429/products/IMG_2347_1000x1000.jpg?v=1522633016"
                )
                for (path in images) {
                    dynamicMediaList.add(MediaInfo.mediaLoader(PicassoImageLoader(path)))
                }
                galleryView = ScrollGalleryView
                    .from(binding.scrollGalleryView)
                    .settings(
                        GallerySettings
                            .from(parentFragmentManager)
                            .thumbnailSize(250)
                            .enableZoom(true)
                            .build()
                    )
                    .add(dynamicMediaList)
                    .build();
            }

        }
    }

//    private fun setUpFullScreen() {
//        activity?.let {
//            it.window.apply {
//                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                statusBarColor = Color.TRANSPARENT
//            }
//        }
//    }

}