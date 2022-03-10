package com.ayata.clad.utils
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import java.io.ByteArrayOutputStream
import java.io.File


class PreferenceHandler {
    companion object Data {
        private const val DARK_THEME="dark theme"
        private const val CURRENCY="currency"
        private const val TOKEN="bearer token"
        const val EMAIL = "email"
        const val USERNAME = "username"
        const val IMAGE = "image"
        private fun getSharedPreference(ctx: Context?): SharedPreferences? {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
        }
        private fun editor(context: Context, const: String, string: String) {
            getSharedPreference(
                context
            )?.edit()?.putString(const, string)?.apply()
        }
        fun savePhoneNumber(context: Context?,tokenid:String?){
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPref.edit()
            editor.putString(Constants.PHONE, tokenid)
            editor.apply()
        }
        fun getEmail(context: Context) = getSharedPreference(
            context
        )?.getString(EMAIL, "")

        fun setEmail(context: Context, email: String) {
            editor(
                context,
                EMAIL,
                email
            )
        }
        fun setUsername(context: Context, username: String) {
            editor(
                context,
                USERNAME,
                username
            )
        }
        fun getUsername(context: Context) = getSharedPreference(
            context
        )?.getString(USERNAME, "")
        fun setImage(context: Context, imageBitmap: Bitmap?) {
            editor(
                context,
                IMAGE,
                encodeTobase64(imageBitmap!!)!!
            )
        }
        // method for bitmap to base64
        fun encodeTobase64(image: Bitmap): String? {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            val imageEncoded = Base64.encodeToString(b, Base64.DEFAULT)
            Log.d("Image Log:", imageEncoded)
            return imageEncoded
        }
        fun getImageDecoded(context: Context): Bitmap? {
            return decodeBase64(getImage(context))
        }
        // method for base64 to bitmap
        fun decodeBase64(input: String?): Bitmap? {
            val decodedByte = Base64.decode(input, 0)
            return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.size)
        }


//    fun getImage(context: Context) = getSharedPreference(
//        context
//    )?.getString(IMAGE, "")

        fun getImage(context: Context) = getSharedPreference(
            context
        )?.getString(IMAGE, "")
        fun getPhoneNumber(context: Context?):String?
        {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPref.getString(Constants.PHONE, "")
        }

        fun setTheme(context: Context?,isDark:Boolean){
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPref.edit()
            editor.putBoolean(DARK_THEME, isDark)
            editor.apply()
        }

        fun isThemeDark(context: Context?):Boolean
        {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPref.getBoolean(DARK_THEME, false)
        }

        fun setCurrency(context: Context?,currency:String){
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPref.edit()
            editor.putString(CURRENCY, currency)
            editor.apply()
        }

        fun getCurrency(context: Context?): String?
        {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPref.getString(CURRENCY, "npr")
        }

        fun setToken(context: Context?,token:String){
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPref.edit()
            editor.putString(TOKEN, token)
            editor.apply()
        }

        fun getToken(context: Context?): String?
        {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPref.getString(TOKEN, "")
        }
        fun logout(context: Context) {
            val preferences =  PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences?.edit()
            editor?.clear()
            editor?.commit()
            deleteCache(context)
        }
        fun deleteCache(context: Context) {
            try {
                val dir = context.cacheDir
                deleteDir(dir)
            } catch (e: Exception) {
            }
        }

        fun deleteDir(dir: File?): Boolean {
            return if (dir != null && dir.isDirectory) {
                val children = dir.list()
                for (i in children.indices) {
                    val success: Boolean = deleteDir(File(dir, children[i]))
                    if (!success) {
                        return false
                    }
                }
                dir.delete()
            } else if (dir != null && dir.isFile) {
                dir.delete()
            } else {
                false
            }
        }



    }
}