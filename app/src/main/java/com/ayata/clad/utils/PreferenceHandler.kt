package com.ayata.clad.utils
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate


class PreferenceHandler {
    companion object Data {
        private const val DARK_THEME="dark theme"
        private const val CURRENCY="currency"
        fun savePhoneNumber(context: Context?,tokenid:String?){
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPref.edit()
            editor.putString(Constants.PHONE, tokenid)
            editor.apply()
        }

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




    }
}