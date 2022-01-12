package com.ayata.clad.login

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.pedant.SweetAlert.SweetAlertDialog
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.preference.DataStoreManager
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentVerificationBinding
import com.ayata.clad.login.response.VerificationResponse
import com.ayata.clad.login.viewmodel.LoginViewModel
import com.ayata.clad.login.viewmodel.LoginViewModelFactory
import com.ayata.clad.utils.PreferenceHandler
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

const val TAG="FragmentVerification"
class FragmentVerification : Fragment() {
    private lateinit var loginViewModel: LoginViewModel

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
        setupViewModel()

        activityFragmentVerificationBinding.spinKit.visibility = View.GONE
        initTimer()
        //on pin enter called
        activityFragmentVerificationBinding.pinview.setOtpCompletionListener {
            if(it.isNullOrEmpty() || it.isNullOrBlank()){
                return@setOtpCompletionListener
            }
            if(it.trim().length!=4){
                return@setOtpCompletionListener
            }
            activityFragmentVerificationBinding.spinKit.visibility = View.VISIBLE
            Log.d(TAG, "onCreateView: $it")
            verificationApi(it)
        }
        activityFragmentVerificationBinding.btnresend.setOnClickListener {

            OncallReSendotp(phone)
        }

        activityFragmentVerificationBinding.relativeLayout.setOnClickListener {
            OncloseClick()
        }

        return activityFragmentVerificationBinding.root
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProviders.of(
            this,
            LoginViewModelFactory(ApiRepository(ApiService.getInstance()))
        ).get(LoginViewModel::class.java)

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

    private fun verificationApi(code: String?) {
        if (!code.isNullOrBlank() && !code.isNullOrEmpty()) {
            if (code.trim().length != 4) {
                activityFragmentVerificationBinding.spinKit.visibility = View.GONE
                Toast.makeText(
                    context,
                    "Error: Invalid Pin Code ${code.trim()}",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                //Here is the code for retro api to check verfication code the response token is store in prefrencehandler
                loginViewModel.otpVerification(phone, code!!.trim())
                //observe
                loginViewModel.doOTPCheck().observe(viewLifecycleOwner, Observer {
                    when (it.status) {
                        Status.SUCCESS -> {
                            Log.d(TAG, "verificationApi: ${it.data}")
                            activityFragmentVerificationBinding.spinKit.visibility = View.GONE
                            try {
                                val verificationResponse = Gson().fromJson<VerificationResponse>(
                                    it.data,
                                    VerificationResponse::class.java
                                )
                                if (verificationResponse.details != null) {
                                    GlobalScope.launch(Dispatchers.IO) {
                                        DataStoreManager(requireContext()).saveToken(
                                            verificationResponse.details.token
                                        )
                                        startActivity(Intent(context, MainActivity::class.java))
                                        activity?.finish()
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }

                        }
                        Status.LOADING -> {
                        }
                        Status.ERROR -> {
                            //Handle Error
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                            Log.d(TAG, "verificationApi: ${it.message}")
                            activityFragmentVerificationBinding.spinKit.visibility = View.GONE
//                        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
//                            .setTitleText("Oops...")
//                            .setContentText("Something went wrong!")
//                            .show()
                        }
                    }
                })
            }

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


}