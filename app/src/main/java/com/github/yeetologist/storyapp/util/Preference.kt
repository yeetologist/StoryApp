package com.github.yeetologist.storyapp.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Preference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }
    }

    suspend fun saveToken(userToken: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = userToken
        }
    }

    companion object {
        private val TOKEN = stringPreferencesKey("token")

        @Volatile
        private var INSTANCE: Preference? = null

        fun getInstance(dataStore: DataStore<Preferences>): Preference {
            return INSTANCE ?: synchronized(this) {
                val instance = Preference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
