package com.ayata.clad

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.viewbinding.ViewBinding
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.login.viewmodel.LoginViewModel
import com.ayata.clad.login.viewmodel.LoginViewModelFactory
import com.ayata.clad.utils.DialogWithData
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.ProgressDialog
import com.ayata.clad.utils.SharedViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

abstract class BaseActivity<B : ViewBinding>(val bindingFactory: (LayoutInflater) -> B) :
    AppCompatActivity(), CustomExceptionHandler {
    private lateinit var binding: B
    private var referCode: String = ""
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindingFactory(layoutInflater)
        setContentView(binding.root)
        setUpGoogle()
        setupViewModel()

    }

    protected fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 100)
    }

    private fun setUpGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_API_KEY)
            .requestEmail()
            .build()
        // Initialize lateinit vars
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            100 -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    login(currentUser = account)
//                    saveUserCredential(account)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(com.ayata.clad.login.TAG, "Google sign in failed", e)
                }
            }
        }
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProviders.of(
            this,
            LoginViewModelFactory(ApiRepository(ApiService.getInstance(this)))
        ).get(LoginViewModel::class.java)

    }

    private fun login(currentUser: GoogleSignInAccount) {
//        (binding).spinKit.visibility= View.VISIBLE
        //check for referal code
        Log.d("testreferal", "login: imhere");
        val sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        try {
            DialogWithData(0,this).show(supportFragmentManager, DialogWithData.TAG)
        } catch (e: Throwable) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }

        sharedViewModel.name.observe(this, Observer {
            Log.d("testreferal", "login: imhere with referal " + it);
            referCode = it
            progressDialog = ProgressDialog.newInstance("", "")
            progressDialog.show(supportFragmentManager, "login_progress")
            val id = currentUser.idToken
            if (id != null) {
                Log.d(com.ayata.clad.login.TAG, "login:token   " + id);
                Log.d(com.ayata.clad.login.TAG, "token end  refer_code ");
                loginViewModel.login(id, referCode)
            } else {
                Toast.makeText(this, "null id", Toast.LENGTH_LONG).show()

            }
            //observe
            loginViewModel.doLogin().observe(this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        progressDialog.dismiss()
//                    activityOnboarding.spinKit.visibility= View.GONE
                        //{"message":"Token created successfully","details":{"token":"0d43a2fcea8a4fcd14481cb8018a4890f0330126","profile":{"email":"srezty@gmail.com","first_name":"Shristy","last_name":"Shakya"}}}
                        val email =
                            it.data?.get("details")?.asJsonObject?.get("profile")?.asJsonObject?.get(
                                "email"
                            )
                                .toString()
                        val firstname =
                            it.data?.get("details")?.asJsonObject?.get("profile")?.asJsonObject?.get(
                                "first_name"
                            )
                                .toString()
                        val lastname =
                            it.data?.get("details")?.asJsonObject?.get("profile")?.asJsonObject?.get(
                                "last_name"
                            )
                                .toString()
                        //contact_number
                        val contact_number: String? =
                            it.data?.get("details")?.asJsonObject?.get("profile")?.asJsonObject?.get(
                                "phone_no"
                            )
                                ?.toString()
                        //dob
                        val dob: String? =
                            it.data?.get("details")?.asJsonObject?.get("profile")?.asJsonObject?.get(
                                "dob"
                            )
                                ?.toString()
                        val gender: String? =
                            it.data?.get("details")?.asJsonObject?.get("profile")?.asJsonObject?.get(
                                "gender"
                            )
                                ?.toString()

                        val token = it.data?.get("details")?.asJsonObject?.get("token").toString()
                        saveUserCredential(
                            currentUser,
                            firstname.removeDoubleQuote(),
                            lastname.removeDoubleQuote(),
                            email.removeDoubleQuote(),
                            token.removeDoubleQuote(),
                            contact_number?.removeDoubleQuote(),
                            dob?.removeDoubleQuote(),
                            gender?.removeDoubleQuote()
                        )
                    }
                    Status.LOADING -> {
//                    activityOnboarding.spinKit.visibility= View.VISIBLE
                    }
                    Status.ERROR -> {
                        //Handle Error
                        progressDialog.dismiss()
//                    activityOnboarding.spinKit.visibility= View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            })
        })
    }

    //remove
    fun String.removeDoubleQuote() = this?.let { this.replace("\"", "") }


    fun saveUserCredential(
        currentUser: GoogleSignInAccount,
        firstname: String,
        lastname: String,
        email: String,
        token: String,
        contact: String?,
        dob: String?,
        gender: String?
    ) {
        PreferenceHandler.setEmail(this, email)
        PreferenceHandler.setUsername(this, firstname + " " + lastname)
        PreferenceHandler.setFirstName(this, firstname)
        PreferenceHandler.setLastName(this, lastname)
        PreferenceHandler.setToken(this, "Token " + token)
        Log.d(com.ayata.clad.login.TAG, "saveUserCredential: " + currentUser.photoUrl.toString());
        PreferenceHandler.setGender(this, gender ?: "")
        PreferenceHandler.setDOB(this, dob ?.let { if(it.equals("null")) "" else it }?: kotlin.run { ""})
        PreferenceHandler.setPhone(this, contact?.let { if(it.equals("null")) "" else it }?: kotlin.run { ""})
        getBitmapFromURL(currentUser.photoUrl.toString())
    }

    fun getBitmapFromURL(src: String?) {
        object : AsyncTask<String?, Void?, Boolean>() {
            override fun onPostExecute(aboolean: Boolean) {
                val intent = Intent(this@BaseActivity, MainActivity::class.java)
                startActivity(intent)
                this@BaseActivity?.finish()
            }

            override fun doInBackground(vararg p0: String?): Boolean {
                return try {
                    val url = URL(src)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input = connection.inputStream
                    val myBitmap = BitmapFactory.decodeStream(input)
                    PreferenceHandler.setImage(this@BaseActivity, myBitmap)
                    true
                } catch (e: IOException) {
                    e.printStackTrace()
                    false
                }
            }
        }.execute(src)
    }

    override fun handleException(e: String) {
        Toast.makeText(this,e,Toast.LENGTH_SHORT).show()
    }
}

interface CustomExceptionHandler {
    fun handleException(e: String)
}
