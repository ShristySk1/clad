package com.ayata.clad.password

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentVerificationBinding
import kotlin.math.log
import cn.pedant.SweetAlert.SweetAlertDialog
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener
import com.ayata.clad.MainActivity
import com.ayata.clad.utils.PreferenceHandler

class FragmentVerification : Fragment() {

    lateinit var activityFragmentVerificationBinding: FragmentVerificationBinding
    var phone=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activityFragmentVerificationBinding= FragmentVerificationBinding.inflate(inflater,container,false)
        InitValues()

        activityFragmentVerificationBinding.spinKit.visibility=View.GONE
        initTimer()
        //on pin enter called
        activityFragmentVerificationBinding.pinview.setOtpCompletionListener {
            activityFragmentVerificationBinding.spinKit.visibility=View.VISIBLE
            Log.d(TAG, "onCreateView: "+it)
            Checkwithapi(it)
        }
        activityFragmentVerificationBinding.btnresend.setOnClickListener {

            OncallReSendotp(phone)
        }

        activityFragmentVerificationBinding.relativeLayout.setOnClickListener {
            OncloseClick()
        }

        return activityFragmentVerificationBinding.root
    }

    private fun OncallReSendotp(phone: String) {
        //Here is the code for retro api call
        SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
            .setContentText("Wait for otp code")
            .show()
        initTimer()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                // This method will be executed once the timer is over


            },
            1000 // value in milliseconds
        )

    }

    private fun Checkwithapi(it: String?) {
        if(!it.isNullOrBlank())
        {
            //Here is the code for retro api to check verfication code the response token is store in prefrencehandler


            Handler(Looper.getMainLooper()).postDelayed(
                {
                    // This method will be executed once the timer is over
                    if(it.equals("1111")) {
                        activityFragmentVerificationBinding.spinKit.visibility = View.GONE
                        val pDialog =
                            SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                        pDialog.titleText = "Loading ..."
                        pDialog.setCancelable(true)
                        pDialog.show()
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                // This method will be executed once the timer is over
                                startActivity(Intent(context,MainActivity::class.java))
                                activity?.finish()

                            },
                            500 // value in milliseconds
                        )



                    }else{
                        activityFragmentVerificationBinding.spinKit.visibility = View.GONE
                        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Something went wrong!")
                            .show()
                    }
                },
                1000 // value in milliseconds
            )
        }

    }

    private fun InitValues()
    {
        if(!PreferenceHandler.getPhoneNumber(context).isNullOrEmpty())
            phone=PreferenceHandler.getPhoneNumber(context).toString()
        Log.d(TAG, "InitValues: "+phone)

    }

    fun  initTimer()
    {
        var timer = object: CountDownTimer(9000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                activityFragmentVerificationBinding.textTimer.text="New code in "+(millisUntilFinished / 1000).toString()+" seconds"
               // tvTimer.setText("seconds remaining: " + millisUntilFinished / 1000)

            }

            override fun onFinish() {
               // tvTimer.setText("done!")
            }
        }
        timer.start()
    }

    private fun OncloseClick()
    {
        parentFragmentManager.popBackStack()
    }



}