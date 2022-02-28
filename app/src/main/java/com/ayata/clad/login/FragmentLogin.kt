package com.ayata.clad.login

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.preference.DataStoreManager
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentLoginBinding
import com.ayata.clad.login.viewmodel.LoginViewModel
import com.ayata.clad.login.viewmodel.LoginViewModelFactory
import com.ayata.clad.onboarding.ActivityOnboarding
import com.ayata.clad.utils.CheckForNetwork
import com.ayata.clad.utils.PreferenceHandler
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Circle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentLogin : Fragment() {
    private val TAG="FragmentLogin"
    private lateinit var loginViewModel: LoginViewModel
    private var phone: String = ""
    private var countrycode: String = ""
    lateinit var rootView: View
    lateinit var activityFragmentLoginBinding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activityFragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        setupViewModel()
        activityFragmentLoginBinding.btnClose.setOnClickListener {
            startActivity(Intent(context, ActivityOnboarding::class.java))
            activity?.finish()
        }
        val circle: Sprite = Circle()
        activityFragmentLoginBinding.spinKit.setIndeterminateDrawable(circle)
        activityFragmentLoginBinding.btnnext.setOnClickListener {
            if(CheckForNetwork().haveNetworkConnection(requireContext())){
                Log.d(TAG, "onCreateView: connected")
                if (validatePhone()) {
                    countrycode = activityFragmentLoginBinding.countryCodePicker.fullNumber
                    Log.d(TAG, "onCreateView: $phone  +$countrycode")
                    activityFragmentLoginBinding.spinKit.visibility = View.VISIBLE
                    //phone api call
                    phoneApi(phone)
                }
            }else{
                Log.d(TAG, "onCreateView: not connected")
                Toast.makeText(context,"Check Your Internet Connection",Toast.LENGTH_SHORT).show()
            }
            
        }


        activityFragmentLoginBinding.edittextPhonenumber.addTextChangedListener(mTextEditorWatcher)
        return activityFragmentLoginBinding.root
    }

    private fun getPhone(){
        GlobalScope.launch(Dispatchers.IO) {
            DataStoreManager(requireContext()).getPhoneNumber().catch { e ->
                e.printStackTrace()
                Log.d(TAG, "getPhone: ${e.message}")
            }.collect {
                withContext(Dispatchers.Main) {
                    phone=it
                    Log.d(TAG, "initValues: $phone")
                }
            }
        }
    }

    private fun validatePhone(): Boolean {
        phone = activityFragmentLoginBinding.textLayoutMobile.editText!!.text.toString().trim()
        return when {
            phone.isEmpty() or (phone == " ") -> {
                activityFragmentLoginBinding.textLayoutMobile.error = "Field can't be empty"
                false
            }
            phone.length != 10 -> {
                activityFragmentLoginBinding.textLayoutMobile.error =
                    "Field should contain 10 digits"
                false
            }
            else -> {
                activityFragmentLoginBinding.textLayoutMobile.error = null
                activityFragmentLoginBinding.textLayoutMobile.isErrorEnabled = false
                true
            }
        }
    }

    private val mTextEditorWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //This sets a textview to the current length
            if (count != before) {
                activityFragmentLoginBinding.textLayoutMobile.error = null
                activityFragmentLoginBinding.textLayoutMobile.isErrorEnabled = false
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProviders.of(
            this,
            LoginViewModelFactory(ApiRepository(ApiService.getInstance()))
        ).get(LoginViewModel::class.java)

    }

    private fun phoneApi(phone:String){
        loginViewModel.phoneAPI(phone)
        //observe
        loginViewModel.doPhone().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "login: ${it.data}")
                    activityFragmentLoginBinding.spinKit.visibility = View.GONE
                    val dataStoreManager= DataStoreManager(requireContext())
                    GlobalScope.launch(Dispatchers.IO) {
                        dataStoreManager.savePhoneNumber(phone)
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                            .replace(R.id.fragments_passwords, FragmentVerification())
                            .addToBackStack(null).commit()
                    }
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "phoneApi: ${it.message}")
                    activityFragmentLoginBinding.spinKit.visibility = View.GONE

                }
            }
        })
    }

}