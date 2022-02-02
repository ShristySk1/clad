package com.ayata.clad.profile.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.preference.DataStoreManager
import com.ayata.clad.databinding.ActivityOnboardingBinding
import com.ayata.clad.databinding.DialogFilterBinding
import com.ayata.clad.databinding.FragmentAccountBinding
import com.ayata.clad.filter.filterdialog.AdapterFilterContent
import com.ayata.clad.filter.filterdialog.MyFilterContentViewItem
import com.ayata.clad.onboarding.ActivityOnboarding
import com.ayata.clad.profile.address.FragmentAddressAdd
import com.ayata.clad.profile.edit.FragmentProfileEdit
import com.ayata.clad.utils.PreferenceHandler
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FragmentAccount : Fragment() {
    lateinit var binding: FragmentAccountBinding
    val TAG = "FragmentAccount"

    private var prevTheme:String=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentAccountBinding.inflate(inflater, container, false)
        prevTheme = if(PreferenceHandler.isThemeDark(context)){
            "dark"
        }else{
            "light"
        }
        initView()
        setUpRecyclerView()
        return binding.root
    }

    private fun initView() {
        binding.btnLogOut.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                DataStoreManager(requireContext()).logout()
                startActivity(Intent(context, ActivityOnboarding::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            }
        }
    }


    private fun setUpRecyclerView() {
        binding.rvAccount.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = AdapterAccount(
                context,
                arrayListOf(
                    ModelAccount(0, 2, "PERSONAL INFORMATION"),
                    ModelAccount(1, 2, "ADDRESS BOOK"),
                    ModelAccount(2, 1, "APP SETTINGS"),
                    ModelAccount(3, 2, "COUNTRY & LANGUAGE"),
                    ModelAccount(4, 2, "NOTIFICATION"),
                    ModelAccount(8, 2, "THEME"),
                    ModelAccount(9, 2, "CURRENCY"),
                    ModelAccount(5, 1, "PRIVACY"),
                    ModelAccount(6, 2, "TERMS AND CONDITIONS"),
                    ModelAccount(7, 2, "PRIVACY POLICY"),
                )
            ).also {
                it.setAccountClickListener { model->
                    Log.d(TAG, "setUpRecyclerView: " + model.textData);
                    when (model.position) {
                        0 -> {//PERSONAL INFORMATION
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.main_fragment,FragmentProfileEdit()).addToBackStack(null).commit()
                        }
                        1 -> {//ADDRESS BOOK
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.main_fragment,FragmentAddressAdd()).addToBackStack(null).commit()
                        }
                        3 -> {//COUNTRY & LANGUAGE
                        }
                        4 -> {//NOTIFICATION
                        }
                        6 -> {//TERMS AND CONDITIONS
                        }
                        7 -> {//PRIVACY POLICY
                        }
                        8->{
                            //theme
                            showDialogTheme()
                        }
                        9->{
                            showDialogCurrency()
                        }
                    }
                }
            }
        }
    }

    private fun showDialogTheme(
    ) {
        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        val list= listOf<MyFilterContentViewItem.SingleChoice>(MyFilterContentViewItem.SingleChoice("Light Theme", false),
                MyFilterContentViewItem.SingleChoice("Dark Theme", false))

        if(PreferenceHandler.isThemeDark(context)){
            list[1].isSelected=true
            list[0].isSelected=false
        }else{
            list[0].isSelected=true
            list[1].isSelected=false
        }

        val adapterFilterContent = AdapterFilterContent(
            context, list
        ).also { adapter ->
            adapter.setCircleClickListener { data ->
                for (item in list) {
                    item.isSelected = item == data
                }
                adapter.notifyDataSetChanged()
                if(!data.title.contains(prevTheme,true)) {
                    if (data.title.contains("dark", true)) {
                        PreferenceHandler.setTheme(context, true)
                        prevTheme = "dark"
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }else{
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

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(getString(R.string.profile),
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
        val list= listOf<MyFilterContentViewItem.SingleChoice>(
            MyFilterContentViewItem.SingleChoice("Nepali (NPR)", false),
            MyFilterContentViewItem.SingleChoice("Dollar (USD)", false))

        if(PreferenceHandler.getCurrency(context).equals(getString(R.string.npr_case),true)){
            list[0].isSelected=true
            list[1].isSelected=false
        }else{
            list[1].isSelected=true
            list[0].isSelected=false
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
                }else{
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