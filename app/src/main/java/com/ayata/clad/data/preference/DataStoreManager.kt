package com.ayata.clad.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {
    private val USER_DATASTORE="USER DATASTORE"

    companion object {

        val TOKEN = stringPreferencesKey("TOKEN_KEY")
        val PHONE_NUMBER = stringPreferencesKey("PHONE_NUMBER")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_DATASTORE)

    suspend fun savePhoneNumber(phone: String) {
        context.dataStore.edit {
            it[PHONE_NUMBER] = phone
        }
    }

    suspend fun getPhoneNumber() = context.dataStore.data.map {
            it[PHONE_NUMBER]?:""
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit {
            it[TOKEN] = "Bearer $token"
        }
    }

    suspend fun getToken() = context.dataStore.data.map {
        it[TOKEN]?:""
    }

    suspend fun logout(){
        context.dataStore.edit {
            it.clear()
        }
    }

}