package com.ayata.clad.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.preference.DataStoreManager
import com.ayata.clad.databinding.FragmentVerificationBinding
import com.ayata.clad.utils.PreferenceHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

class FragmentVerification : Fragment() {

    lateinit var activityFragmentVerificationBinding: FragmentVerificationBinding
    var phone = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activityFragmentVerificationBinding =
            FragmentVerificationBinding.inflate(inflater, container, false)
        initValues()

        activityFragmentVerificationBinding.spinKit.visibility = View.GONE
        initTimer()
        //on pin enter called
        activityFragmentVerificationBinding.pinview.setOtpCompletionListener {
            activityFragmentVerificationBinding.spinKit.visibility = View.VISIBLE
            Log.d(TAG, "onCreateView: $it")
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
        if (!it.isNullOrBlank()) {
            //Here is the code for retro api to check verfication code the response token is store in prefrencehandler


            Handler(Looper.getMainLooper()).postDelayed(
                {
                    // This method will be executed once the timer is over
                    if (it.equals("1111")) {
//                        activityFragmentVerificationBinding.spinKit.visibility = View.GONE
//                        val pDialog =
//                            SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
//                        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
//                        pDialog.titleText = "Loading ..."
//                        pDialog.setCancelable(true)
//                        pDialog.show()
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                // This method will be executed once the timer is over
                                startActivity(Intent(context, MainActivity::class.java))
                                activity?.finish()

                            },
                            500 // value in milliseconds
                        )


                    } else {
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

    private fun initValues() {

        GlobalScope.launch(Dispatchers.IO) {
            DataStoreManager(requireContext()).getPhoneNumber().catch { e ->
                e.printStackTrace()
            }.collect {
                withContext(Dispatchers.Main) {
                    phone=it
                    Log.d(TAG, "initValues: $phone")
                    activityFragmentVerificationBinding.textView2.text=
                        SpannableStringBuilder().append(getString(R.string.str_vemessage)).append(" at ")
                            .bold { color(ContextCompat.getColor(requireContext(),R.color.black)) {
                                append("+977 $phone")
                            }}
                }
            }
        }
    }

    fun initTimer() {
        val timer = object : CountDownTimer(9000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                activityFragmentVerificationBinding.textTimer.text =
                    "New code in " + (millisUntilFinished / 1000).toString() + " seconds"
                // tvTimer.setText("seconds remaining: " + millisUntilFinished / 1000)

            }

            override fun onFinish() {
                // tvTimer.setText("done!")
            }
        }
        timer.start()
    }

    private fun OncloseClick() {
        parentFragmentManager.popBackStack()
    }

    private fun saveToDataStore(){
        val dataStoreManager=DataStoreManager(requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            dataStoreManager.savePhoneNumber("1000000000")
        }

        GlobalScope.launch(Dispatchers.IO) {
            dataStoreManager.getPhoneNumber().catch { e ->
                e.printStackTrace()
            }.collect {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

}