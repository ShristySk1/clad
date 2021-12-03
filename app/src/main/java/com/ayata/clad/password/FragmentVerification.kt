package com.ayata.clad.password

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentVerificationBinding
import kotlin.math.log

class FragmentVerification : Fragment() {

    lateinit var activityFragmentVerificationBinding: FragmentVerificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activityFragmentVerificationBinding= FragmentVerificationBinding.inflate(inflater,container,false)
        activityFragmentVerificationBinding.spinKit.visibility=View.GONE
        activityFragmentVerificationBinding.pinview.setOtpCompletionListener {

            Log.d(TAG, "onCreateView: "+it)
        }



        return activityFragmentVerificationBinding.root
    }

}