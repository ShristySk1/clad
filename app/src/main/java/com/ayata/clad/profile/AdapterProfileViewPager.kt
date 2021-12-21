package com.ayata.clad.profile

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder

class AdapterProfileViewPager(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private var fragmentList: ArrayList<Fragment> = ArrayList()

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }

    fun addFragmentList(list: ArrayList<Fragment>) {
        fragmentList.clear()
        fragmentList.addAll(list)
    }

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {

        super.onBindViewHolder(holder, position, payloads)
    }
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}