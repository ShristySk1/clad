package com.ayata.clad.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
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
        initView()
        initAppbar()

        return binding.root
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
        val name=PreferenceHandler.getUsername(requireContext())
        val email=PreferenceHandler.getEmail(requireContext())
        binding.accName.text = name
        binding.accEmail.text = email
        if(PreferenceHandler.getToken(requireContext())!!.isEmpty()){
            binding.accName.text = "Guest User"
            binding.accEmail.text = ""
        }
        Glide.with(requireContext()).asBitmap()
            .load(PreferenceHandler.getImageDecoded(requireContext()))
            .fallback(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .into(binding.ivProfileImage)
        val detail:Details=Details("",PreferenceHandler.getEmail(requireContext())?:"",PreferenceHandler.getUsername(requireContext())?:"","")
        accountiewModel.setAccountDetail(detail)
    }

//    private fun setPreviousData() {
//        viewModel.profileDetailAPI(PreferenceHandler.getToken(context)!!)
//        viewModel.getProfileAPI().observe(viewLifecycleOwner, {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    binding.spinKit.visibility = View.GONE
//                    val jsonObject = it.data
//                    if (jsonObject != null) {
//                        try {
//                            val profileResponse =
//                                Gson().fromJson<UserProfileResponse>(
//                                    jsonObject,
//                                    UserProfileResponse::class.java
//                                )
//                            if (profileResponse.details != null) {
//                                val detail = profileResponse.details
//                                setDataToView(detail)
//
//                            }
//                        } catch (e: Exception) {
//                            Log.d("", "prepareAPI: ${e.message}")
//                        }
//                    }
//
//                }
//                Status.LOADING -> {
//                    binding.spinKit.visibility = View.VISIBLE
//
//                }
//                Status.ERROR -> {
//                    //Handle Error
//                    binding.spinKit.visibility = View.GONE
//                    if (it.message.equals("Unauthorized")) {
//
//                    } else {
//                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
//
//                    }
//                    Log.d("", "home: ${it.message}")
//                }
//            }
//        })
//    }
//
//    private fun setDataToView(detail: Details) {
//        accountiewModel.setAccountDetail(detail)
////        bundle = Bundle()
////        bundle.putSerializable(Constants.EDIT_PROFILE, detail)
//        binding.accEmail.setText(detail.email)
//        binding.accName.setText(detail.fullName)
//        val initials = detail.fullName
//            .split(' ')
//            .mapNotNull { it.firstOrNull()?.toString() }
//            .reduce { acc, s -> acc + s }
//        binding.profileNamePlaceholder.text = initials
//
//    }

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

    private fun initView() {
//        val initials = "Ronesh Shrestha"
//            .split(' ')
//            .mapNotNull { it.firstOrNull()?.toString() }
//            .reduce { acc, s -> acc + s }
//        binding.profileNamePlaceholder.text = initials
        binding.viewPager.setUserInputEnabled(false);

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
//        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                if ((tab?.view?.isEnabled)!!) {
//                    //needs login
//                    binding.viewPager.currentItem = tab.position
//                }else{
//
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//
//            }
//
//        })
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