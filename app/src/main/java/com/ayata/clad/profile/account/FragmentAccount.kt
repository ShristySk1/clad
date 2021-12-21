package com.ayata.clad.profile.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.databinding.ActivityOnboardingBinding
import com.ayata.clad.databinding.FragmentAccountBinding
import com.ayata.clad.onboarding.ActivityOnboarding

class FragmentAccount : Fragment() {
    lateinit var binding: FragmentAccountBinding
    val TAG = "FragmentAccount"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentAccountBinding.inflate(inflater, container, false)
        initView()
        setUpRecyclerView()
        return binding.root
    }

    private fun initView() {
        binding.btnLogOut.setOnClickListener {
            startActivity(Intent(context, ActivityOnboarding::class.java))
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
                    ModelAccount(5, 1, "PRIVACY"),
                    ModelAccount(6, 2, "TERMS AND CONDITIONS"),
                    ModelAccount(7, 2, "PRIVACY POLICY"),
                )
            ).also {
                it.setAccountClickListener {
                    Log.d(TAG, "setUpRecyclerView: " + it.textData);
                    when (it.position) {
                        0 -> {//PERSONAL INFORMATION
                        }
                        1 -> {//ADDRESS BOOK
                        }
                        3 -> {//COUNTRY & LANGUAGE
                        }
                        4 -> {//NOTIFICATION
                        }
                        6 -> {//TERMS AND CONDITIONS
                        }
                        7 -> {//PRIVACY POLICY
                        }
                    }
                }
            }
        }
    }

}