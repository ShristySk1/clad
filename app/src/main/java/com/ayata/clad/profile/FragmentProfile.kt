package com.ayata.clad.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentProfileBinding
import com.ayata.clad.profile.account.FragmentAccount
import com.ayata.clad.profile.giftcard.FragmentGiftCard
import com.ayata.clad.profile.myorder.FragmentMyOrder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FragmentProfile : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private lateinit var listFragment: ArrayList<Fragment>
    private lateinit var adapterProfile: AdapterProfileViewPager
    private lateinit var titles: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentProfileBinding.inflate(inflater, container, false)
        setTabLayout(listOf(FragmentAccount(), FragmentMyOrder(), FragmentGiftCard()), arrayListOf("Accounts","Orders","Gift Card"))
        initView()
        initAppbar()

        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(getString(R.string.profile),
            isSearch = false,
            isProfile = false,
            isClose = true
        )
    }

    private fun initView() {



    }
    private fun setTabLayout(list: List<Fragment>, myTitles: ArrayList<String>) {
        listFragment = ArrayList<Fragment>()
        getDataFromApi()
        listFragment.clear()
        listFragment.addAll(list)
        adapterProfile = AdapterProfileViewPager(this)
        adapterProfile.addFragmentList(listFragment)
        binding.viewPager.adapter = adapterProfile
//        binding.viewPager.offscreenPageLimit = 3
        titles = myTitles
        TabLayoutMediator(
            binding.tabLayout, binding.viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                tab.text = titles[position]
            }
        ).attach()
    }

    private fun getDataFromApi() {
//        list.addAll(data)
//        val currentPosition: Int = binding.viewPager.getCurrentItem()
//        adapterMarketViewPager.notifyDataSetChanged()
//        binding.viewPager.adapter = null
//        binding.viewPager.adapter = adapterMarketViewPager
//        binding.viewPager.currentItem = currentPosition
    }

}