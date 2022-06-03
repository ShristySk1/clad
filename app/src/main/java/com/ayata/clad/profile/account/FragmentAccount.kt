package com.ayata.clad.profile.account

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.BuildConfig
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.DialogFilterBinding
import com.ayata.clad.databinding.FragmentAccountBinding
import com.ayata.clad.filter.filterdialog.AdapterFilterContent
import com.ayata.clad.filter.filterdialog.MyFilterContentViewItem
import com.ayata.clad.login.viewmodel.LoginViewModel
import com.ayata.clad.login.viewmodel.LoginViewModelFactory
import com.ayata.clad.onboarding.ActivityOnboarding
import com.ayata.clad.profile.address.FragmentAddressDetail
import com.ayata.clad.profile.edit.FragmentProfileEdit
import com.ayata.clad.profile.reviews.FragmentMyReviewsList
import com.ayata.clad.utils.PreferenceHandler
import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentAccount : Fragment() {
    lateinit var binding: FragmentAccountBinding
    val TAG = "FragmentAccount"
    private lateinit var loginViewModel: LoginViewModel

    private var prevTheme: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentAccountBinding.inflate(inflater, container, false)
        prevTheme = if (PreferenceHandler.isThemeDark(context) ?: false) {
            "dark"
        } else {
            "light"
        }
        initView()
        setupViewModel()
        setUpRecyclerView()
        return binding.root
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProviders.of(
            this,
            LoginViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        ).get(LoginViewModel::class.java)

    }

    private fun initView() {
        binding.btnLogOut.setOnClickListener {
            //api logout
            PreferenceHandler.getToken(context)?.let { it1 -> loginViewModel.logout(it1) }
//            loginViewModel.dologout().observe(viewLifecycleOwner, {
//                when (it.status) {
//                    Status.SUCCESS -> {
//                        binding.defaultProgress.visibility = View.GONE
//                        Log.d(TAG, "login: ${it.data}")
//
//                            PreferenceHandler.logout(requireContext())
//                            startActivity(
//                                Intent(context, ActivityOnboarding::class.java)
//                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                            )
//                    }
//                    Status.LOADING -> {
//                        binding.defaultProgress.visibility = View.VISIBLE
//                    }
//                    Status.ERROR -> {
//                        //Handle Error
//                        binding.defaultProgress.visibility = View.GONE
//                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
//
//                    }
//                }
//            })
            PreferenceHandler.logout(requireContext())
            startActivity(
                Intent(context, ActivityOnboarding::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )

        }
    }

    private val listAccount = ArrayList<ModelAccount>()
    private fun prepareListAccount() {
        listAccount.clear()
        listAccount.add(ModelAccount(0, 2, "PERSONAL INFORMATION"))
        listAccount.add(ModelAccount(1, 2, "ADDRESS BOOK"))
        listAccount.add(ModelAccount(3, 2, "MY REVIEWS"))
        listAccount.add(ModelAccount(2, 1, "APP SETTINGS"))
        listAccount.add(ModelAccount(4, 2, "NOTIFICATION"))
        listAccount.add(ModelAccount(8, 2, "THEME"))
//        listAccount.add(ModelAccount(9, 2, "CURRENCY"))
        listAccount.add(ModelAccount(5, 1, "GUIDE"))
        listAccount.add(ModelAccount(6, 2, "TERMS AND CONDITIONS"))
        listAccount.add(ModelAccount(7, 2, "PRIVACY POLICY"))
        listAccount.add(ModelAccount(10, 2, "RETURN POLICY"))
        listAccount.add(ModelAccount(11, 2, "HOW TO ORDER"))
//        val listAccount= arrayListOf(
//            ModelAccount(0, 2, "PERSONAL INFORMATION"),
//            ModelAccount(1, 2, "ADDRESS BOOK"),
//            ModelAccount(2, 1, "APP SETTINGS"),
////                    ModelAccount(3, 2, "COUNTRY & LANGUAGE"),
//            ModelAccount(4, 2, "NOTIFICATION"),
//            ModelAccount(8, 2, "THEME"),
//            ModelAccount(9, 2, "CURRENCY"),
//            ModelAccount(5, 1, "GUIDE"),
//            ModelAccount(6, 2, "TERMS AND CONDITIONS"),
//            ModelAccount(7, 2, "PRIVACY POLICY"),
//            ModelAccount(10, 2, "RETURN POLICY"),
//            ModelAccount(11, 2, "HOW TO ORDER")
//        )
    }

    private fun setUpRecyclerView() {
        prepareListAccount()
        val adapterAccount = AdapterAccount(
            requireContext(),
            listAccount
        ).also {
            it.setAccountClickListener { model ->
                Log.d(TAG, "setUpRecyclerView: " + model.textData)
                when (model.position) {
                    0 -> {//PERSONAL INFORMATION
                        if (PreferenceHandler.getToken(requireContext()) != "") {
                            //check if we got our data
                            val fragmentProfileEdit = FragmentProfileEdit()
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.main_fragment, fragmentProfileEdit)
                                .addToBackStack(null)
                                .commit()
                        } else {
                            (activity as MainActivity).showDialogLogin()
                        }

                    }
                    1 -> {//ADDRESS BOOK
                        if (PreferenceHandler.getToken(requireContext()) != "") {
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.main_fragment, FragmentAddressDetail())
                                .addToBackStack(null).commit()
                        } else {
                            (activity as MainActivity).showDialogLogin()
                        }
                    }
                    3 -> {//REVIEWS
                        if (PreferenceHandler.getToken(requireContext()) != "") {
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.main_fragment, FragmentMyReviewsList())
                                .addToBackStack(null).commit()
                        } else {
                            (activity as MainActivity).showDialogLogin()
                        }
                    }
                    4 -> {//NOTIFICATION
                    }
                    6 -> {//TERMS AND CONDITIONS

                        activity?.let {
                            it.supportFragmentManager.beginTransaction().replace(
                                R.id.main_fragment,
                                WebViewFragment.newInstance(BuildConfig.TERMS_AND_CONDITIONS,"Terms And Conditions")
                            ).addToBackStack(null).commit()
                        }
                    }
                    7 -> {//PRIVACY POLICY

                        activity?.let {
                            it.supportFragmentManager.beginTransaction().replace(
                                R.id.main_fragment,
                                WebViewFragment.newInstance(BuildConfig.PRIVACY_POLICY,"Privacy Policy")
                            ).addToBackStack(null).commit()
                        }
                    }
                    8 -> {
                        //theme
                        showDialogTheme()
                    }
                    9 -> {
                        showDialogCurrency()
                    }
                    10 -> {//Return policy
                    }
                    11 -> {//how to order
                    }
                }
            }
        }
        binding.rvAccount.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = adapterAccount
        }
        Log.d(TAG, "setUpRecyclerView: " + listAccount.size + "---" + adapterAccount.list.size)
    }

    private fun showDialogTheme(
    ) {
        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        val list = listOf<MyFilterContentViewItem.SingleChoice>(
            MyFilterContentViewItem.SingleChoice("Light Theme", false),
            MyFilterContentViewItem.SingleChoice("Dark Theme", false)
        )

        if (PreferenceHandler.isThemeDark(context) ?: false) {
            list[1].isSelected = true
            list[0].isSelected = false
        } else {
            list[0].isSelected = true
            list[1].isSelected = false
        }

        val adapterFilterContent = AdapterFilterContent(
            context, list
        ).also { adapter ->
            adapter.setCircleClickListener { data ->
                for (item in list) {
                    item.isSelected = item == data
                }
                adapter.notifyDataSetChanged()
                if (!data.title.contains(prevTheme, true)) {
                    if (data.title.contains("dark", true)) {
                        PreferenceHandler.setTheme(context, true)
                        prevTheme = "dark"
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        PreferenceHandler.setTheme(context, false)
                        prevTheme = "light"
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
        dialogBinding.title.text = "Select Theme"
        dialogBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterFilterContent
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
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

    private fun showDialogCurrency(
    ) {
        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        val list = listOf<MyFilterContentViewItem.SingleChoice>(
            MyFilterContentViewItem.SingleChoice("Nepali (NPR)", false),
            MyFilterContentViewItem.SingleChoice("Dollar (USD)", false)
        )

        if (PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case), true)) {
            list[0].isSelected = true
            list[1].isSelected = false
        } else {
            list[1].isSelected = true
            list[0].isSelected = false
        }

        val adapterFilterContent = AdapterFilterContent(
            context, list
        ).also { adapter ->
            adapter.setCircleClickListener { data ->
                for (item in list) {
                    item.isSelected = item == data
                }
                adapter.notifyDataSetChanged()
                if (data.title.contains("npr", true)) {
                    PreferenceHandler.setCurrency(context, "npr")
                } else {
                    PreferenceHandler.setCurrency(context, "usd")
                }
            }
        }
        dialogBinding.title.text = "Select Currency"
        dialogBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterFilterContent
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

}