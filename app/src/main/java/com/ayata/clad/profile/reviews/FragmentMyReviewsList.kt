package com.ayata.clad.profile.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayata.clad.MainActivity
import com.ayata.clad.databinding.FragmentMyReviewsListBinding
import com.ayata.clad.profile.reviews.adapter.AdapterReviewsViewPager
import com.ayata.clad.profile.reviews.model.ModelReview
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FragmentMyReviewsList : Fragment() {
    private lateinit var titles: Array<String>
    lateinit var binding: FragmentMyReviewsListBinding
    private lateinit var listReviewUnreviewed: ArrayList<ModelReview>
    private lateinit var listReviewed: ArrayList<ModelReview>
    private lateinit var adapterReviewViewPager: AdapterReviewsViewPager
    private lateinit var listFragment: ArrayList<Fragment>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentMyReviewsListBinding.inflate(inflater, container, false)
        initAppbar()
        setTabLayout()
        return binding.root
    }

    private fun initAppbar() {
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "My Reviews",
            textDescription = ""
        )
        (activity as MainActivity).showBottomNavigation(false)
    }

    private fun setTabLayout() {
        getDataFromApi()
        val fragmentUnReviewed = getMyFragment(FragmentMyReviews(), listReviewUnreviewed)
        val fragmentReviewed = getMyFragment(FragmentMyReviews(), listReviewed)
        listFragment= arrayListOf()
        listFragment.clear()
        listFragment.add(fragmentUnReviewed)
        listFragment.add(fragmentReviewed)
        adapterReviewViewPager = AdapterReviewsViewPager(this)
        adapterReviewViewPager.addFragmentList(listFragment)
        binding.viewPager.adapter = adapterReviewViewPager
        titles =
            arrayOf("Unreviewed", "Reviewed")
        TabLayoutMediator(
            binding.tabLayoutLeadershipBoard, binding.viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                tab.text = titles[position]
            }
        ).attach()
    }
    private fun getDataFromApi() {
        val dataReviewed = mutableListOf<ModelReview>(ModelReview("A",true),ModelReview("C",true))
        val dataUnreviewed = mutableListOf<ModelReview>(ModelReview("B",false))
        listReviewUnreviewed= arrayListOf()
        listReviewed= arrayListOf()
        setData(dataUnreviewed, listReviewUnreviewed)
        setData(dataReviewed, listReviewed)
    }

    private fun setData(data: MutableList<ModelReview>, list: ArrayList<ModelReview>) {
        list.clear()
        list.addAll(data)
//        val currentPosition: Int = binding.viewPager.getCurrentItem()
//        adapterReviewViewPager.notifyDataSetChanged()
//        binding.viewPager.adapter = null
//        binding.viewPager.adapter = adapterReviewViewPager
//        binding.viewPager.currentItem = currentPosition
    }

    private fun getMyFragment(
        fragmentToday: Fragment,
        list: ArrayList<ModelReview>
    ): Fragment {
        val bundle = Bundle()
        bundle.putSerializable("datas", list)
        fragmentToday.arguments = bundle
        return fragmentToday
    }

}