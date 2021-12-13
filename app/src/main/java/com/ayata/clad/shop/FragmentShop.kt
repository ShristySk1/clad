package com.ayata.clad.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentShopBinding
import com.google.android.material.tabs.TabLayout


class FragmentShop : Fragment() {

    private lateinit var binding: FragmentShopBinding

    private var listCategory=ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentShopBinding.inflate(inflater, container, false)

        initTabLayout()
        return binding.root
    }

    private fun initTabLayout(){
        listCategory.clear()
        listCategory.add("Men")
        listCategory.add("Women")
        listCategory.add("Kids")

        val tabLayout=binding.tabLayout
        for(tabString in listCategory) {
            tabLayout.addTab(tabLayout.newTab().setText(tabString))
        }

//        tabCustomise()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabString=tab!!.text
//                prepareSearchList(tabString.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    private fun tabCustomise(){
        //margin between two tabs with background
        val tabs = binding.tabLayout.getChildAt(0) as ViewGroup
        for (i in 0 until tabs.childCount ) {
            val tab = tabs.getChildAt(i)
            val layoutParams = tab.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 1f
            layoutParams.marginEnd = 15
            layoutParams.marginStart = 15
            layoutParams.topMargin=32
            layoutParams.bottomMargin=32
            tab.layoutParams = layoutParams
            binding.tabLayout.requestLayout()
        }

    }


}