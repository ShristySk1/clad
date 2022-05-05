package com.ayata.clad.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentProfileBinding
import com.ayata.clad.profile.account.AccountViewModel
import com.ayata.clad.profile.account.FragmentAccount
import com.ayata.clad.profile.edit.response.Details
import com.ayata.clad.profile.giftcard.FragmentGiftCard
import com.ayata.clad.profile.myorder.FragmentMyOrder
import com.ayata.clad.profile.viewmodel.ProfileViewModel
import com.ayata.clad.profile.viewmodel.ProfileViewModelFactory
import com.ayata.clad.utils.PreferenceHandler
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class FragmentProfile : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private lateinit var listFragment: ArrayList<Fragment>
    private lateinit var adapterProfile: AdapterProfileViewPager
    private lateinit var titles: ArrayList<String>
    lateinit var bundle: Bundle

    private lateinit var viewModel: ProfileViewModel
    private lateinit var accountiewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentProfileBinding.inflate(inflater, container, false)
        setUpViewModel()
        setTabLayout(
            listOf(FragmentAccount(), FragmentMyOrder(), FragmentGiftCard()),
            arrayListOf("Accounts", "Orders", "Gift Card")
        )
        initBundle()
        initAppbar()

        return binding.root
    }

    private fun initBundle() {
        arguments?.let {
            val isFromCheckout=it.getBoolean("showOrder")
            if(isFromCheckout){
                binding.viewPager.setCurrentItem(1,false)
            }
        }
    }

    private fun setUpViewModel() {
        viewModel =
            ViewModelProvider(
                this,
                ProfileViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
            )
                .get(ProfileViewModel::class.java)
        accountiewModel = ViewModelProviders.of(requireActivity()).get(AccountViewModel::class.java)

//        setPreviousData()
        setProfile()
    }

    private fun setProfile() {
        val name = PreferenceHandler.getUsername(requireContext())
        val email = PreferenceHandler.getEmail(requireContext())
        Log.d("testemail", "setProfile: "+email);
        binding.accName.text = name
        binding.accEmail.text = email
        if (PreferenceHandler.getToken(requireContext())!!.isEmpty()) {
            binding.accName.text = "Guest User"
            binding.accEmail.text = "xxx xxx"
            val initials = binding.accName.text
                .split(' ')
                .mapNotNull { it.firstOrNull()?.toString() }
                .reduce { acc, s -> acc + s }
            binding.profileNamePlaceholder.text = initials

        }
        Glide.with(requireContext()).asBitmap()
            .load(PreferenceHandler.getImageDecoded(requireContext()))
            .into(binding.ivProfileImage)

    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(
            getString(R.string.profile),
            isSearch = false,
            isProfile = false,
            isClose = true
        )
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
                if (position != 0) {
                    if (PreferenceHandler.getToken(requireContext()) != "") {
                        tab.view.visibility = View.VISIBLE

                    } else {
//                        tab.view.isEnabled = false
                        tab.view.visibility = View.GONE

                    }
                }
            }
        ).attach()
    }

    private fun getDataFromApi() {

    }

}