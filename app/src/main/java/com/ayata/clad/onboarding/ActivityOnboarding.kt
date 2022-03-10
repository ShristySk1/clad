package com.ayata.clad.onboarding

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.ActivityOnboardingBinding
import com.ayata.clad.login.TAG
import com.ayata.clad.login.viewmodel.LoginViewModel
import com.ayata.clad.login.viewmodel.LoginViewModelFactory
import com.ayata.clad.utils.PreferenceHandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class ActivityOnboarding : AppCompatActivity(), AdapaterActivityOnboarding.setOnItemClickListener {
    private lateinit var viewPager2: ViewPager2
    lateinit var activityOnboarding: ActivityOnboardingBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
        setUpFullScreen()
        setupViewModel()
        activityOnboarding = ActivityOnboardingBinding.inflate((layoutInflater))
        setContentView(activityOnboarding.root)
        setUpGoogle()
        viewPager2 = findViewById(R.id.viewpager2)
        val adapter = AdapaterActivityOnboarding(this)
        viewPager2.adapter = adapter
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager2,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab?, position: Int -> }
        ).attach()

        activityOnboarding.btnonboard.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        activityOnboarding.btnongoogle.setOnClickListener {
//            Toast.makeText(this,"Google login",Toast.LENGTH_SHORT).show()
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 100)
    }

    private fun setUpGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("886011284473-bb0c1cfjkaq8rt2grjlktlhsrq6kvde6.apps.googleusercontent.com")
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
                    Log.w(TAG, "Google sign in failed", e)
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
       activityOnboarding.spinKit.visibility=View.VISIBLE
        val id = currentUser.idToken
        if (id != null) {
            Log.d(TAG, "login:token   " + id);
            Log.d(TAG, "token end   ");
            loginViewModel.login(id)
        } else {
            Toast.makeText(this, "null id", Toast.LENGTH_LONG).show()

        }
        //observe
        loginViewModel.doLogin().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    activityOnboarding.spinKit.visibility=View.GONE
                    //{"message":"Token created successfully","details":{"token":"0d43a2fcea8a4fcd14481cb8018a4890f0330126","profile":{"email":"srezty@gmail.com","first_name":"Shristy","last_name":"Shakya"}}}

                    val email =
                        it.data?.get("details")?.asJsonObject?.get("profile")?.asJsonObject?.get("email")
                            .toString()
                    val firstname =
                        it.data?.get("details")?.asJsonObject?.get("profile")?.asJsonObject?.get("first_name")
                            .toString()
                    val lastname =
                        it.data?.get("details")?.asJsonObject?.get("profile")?.asJsonObject?.get("last_name")
                            .toString()
                    val token = it.data?.get("details")?.asJsonObject?.get("token").toString()
                    saveUserCredential(currentUser,firstname.replace("\"", ""), lastname.replace("\"", ""), email.replace("\"", ""), token.replace("\"", ""))
                }
                Status.LOADING -> {
                    activityOnboarding.spinKit.visibility=View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    activityOnboarding.spinKit.visibility=View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun saveUserCredential(currentUser: GoogleSignInAccount,firstname: String, lastname: String, email: String, token: String) {
        PreferenceHandler.setEmail(this, email)
        PreferenceHandler.setUsername(this, firstname+" "+lastname)
        PreferenceHandler.setToken(this, token)
        Log.d(TAG, "saveUserCredential: "+currentUser.photoUrl.toString());
        getBitmapFromURL(currentUser.photoUrl.toString())
    }

    fun getBitmapFromURL(src: String?) {
        object : AsyncTask<String?, Void?, Boolean>() {
            override fun onPostExecute(aboolean: Boolean) {
                val intent = Intent(this@ActivityOnboarding, MainActivity::class.java)
                startActivity(intent)
                this@ActivityOnboarding?.finish()
            }

            override fun doInBackground(vararg p0: String?): Boolean {
                return try {
                    val url = URL(src)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input = connection.inputStream
                    val myBitmap = BitmapFactory.decodeStream(input)
                    PreferenceHandler.setImage(this@ActivityOnboarding, myBitmap)
                    true
                } catch (e: IOException) {
                    e.printStackTrace()
                    false
                }
            }
        }.execute(src)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun setUpFullScreen() {
        this?.let {
            it.window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                statusBarColor = Color.TRANSPARENT
            }
        }
    }

    override fun onItemClick(position: Int) {

    }

}