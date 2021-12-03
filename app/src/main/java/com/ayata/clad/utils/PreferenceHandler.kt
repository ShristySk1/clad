package com.ayata.clad.utils
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager



class PreferenceHandler {
    companion object Data {
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




    }
}