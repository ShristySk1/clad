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
        const val FIRSTNAME = "firstname"
        const val LASTNAME = "lastname"
        const val IMAGE = "image"
        const val SHOW_ONBOARDING="show onboarding"
        const val RECENT_SEARCH="recent search"
        private const val GENDER="Gender"
        private const val DATE_OF_BIRTH="Data of birth"
        private const val PHONE="phone"

        private fun getSharedPreference(ctx: Context?): SharedPreferences? {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
        }
        private fun editor(context: Context, const: String, string: String) {
            getSharedPreference(
                context
            )?.edit()?.putString(const, string)?.apply()
        }
        private fun editorList(context: Context, const: String, string: Set<String>) {
            getSharedPreference(
                context
            )?.edit()?.putStringSet(const, string)?.apply()
        }

        private fun editorInt(context: Context, const: String, int: Int) {
            getSharedPreference(
                context
            )?.edit()?.putInt(const, int)?.apply()
        }
        private fun editorBoolean(context: Context, const: String, boolean: Boolean) {
            getSharedPreference(
                context
            )?.edit()?.putBoolean(const, boolean)?.apply()
        }
        fun savePhoneNumber(context: Context?,tokenid:String?){
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPref.edit()
            editor.putString(Constants.PHONE, tokenid)
            editor.apply()
        }
        fun setIsOnBoarding(context: Context, showOnboarding: Boolean) {
            editorBoolean(
                context,
                SHOW_ONBOARDING,
                showOnboarding
            )
        }
        fun getShowOnBoarding(context: Context) = getSharedPreference(
            context
        )?.getBoolean(SHOW_ONBOARDING, true)
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

        fun setFirstName(context: Context, username: String) {
            editor(
                context,
                FIRSTNAME,
                username
            )
        }
        fun setLastName(context: Context, username: String) {
            editor(
                context,
                LASTNAME,
                username
            )
        }
        fun getLastName(context: Context) = getSharedPreference(
            context
        )?.getString(LASTNAME, "")
        fun getFirstName(context: Context) = getSharedPreference(
            context
        )?.getString(FIRSTNAME, "")

        fun setRecentSearchList(context: Context, username: Set<String>) {
            editorList(
                context,
                RECENT_SEARCH,
                username
            )
        }
        fun getRecentSearchList(context: Context) = getSharedPreference(
            context
        )?.getStringSet(RECENT_SEARCH, setOf())
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

        fun getImage(context: Context) = getSharedPreference(
            context
        )?.getString(IMAGE, "")
        fun setTheme(context: Context?,isDark:Boolean){
            if(context!=null)
            editorBoolean(context,DARK_THEME, isDark)
        }

        fun isThemeDark(context: Context?)=
            getSharedPreference(
                context
            ).let {  it?.getBoolean(DARK_THEME, false)}


        fun setCurrency(context: Context?,currency:String){
            if(context!=null)
                editor(context,CURRENCY, currency)
        }

        fun getCurrency(context: Context?)=
            getSharedPreference(
                context
            )?.getString(CURRENCY, "npr")


        fun setGender(context: Context?,gender:String){
            if(context!=null)
                editor(context, GENDER, gender)
        }

        fun getGender(context: Context?)=
            getSharedPreference(
                context
            )?.getString(GENDER, "")


        fun setDOB(context: Context?,dob:String){
            if(context!=null)
                editor(context, DATE_OF_BIRTH, dob)
        }

        fun getDOB(context: Context?)=
            getSharedPreference(
                context
            )?.getString(DATE_OF_BIRTH, "")

        //phone
        fun setPhone(context: Context?,phone:String){
            if(context!=null)
                editor(context, PHONE, phone)
        }

        fun getPhone(context: Context?)=
            getSharedPreference(
                context
            )?.getString(PHONE, "")



        fun setToken(context: Context?,token:String){
            if(context!=null)
                editor(context,TOKEN, token)
        }

        fun getToken(context: Context?)=
            getSharedPreference(
                context
            )?.getString(TOKEN, "")
        fun logout(context: Context) {
            val preferences = getSharedPreference(context)
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