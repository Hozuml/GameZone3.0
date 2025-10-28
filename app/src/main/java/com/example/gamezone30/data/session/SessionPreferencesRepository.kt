package com.example.gamezone30.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Creamos la instancia de DataStore para toda la app
val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session_preferences")

class SessionPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val REMEMBER_SESSION = booleanPreferencesKey("remember_session")
        val USER_FULL_NAME = stringPreferencesKey("user_full_name")
    }

    val rememberSessionFlow: Flow<Boolean> = dataStore.data.map {
        preferences -> preferences[Keys.REMEMBER_SESSION] ?: false
    }

    val userFullNameFlow: Flow<String?> = dataStore.data.map {
        preferences -> preferences[Keys.USER_FULL_NAME]
    }

    suspend fun setRememberSession(rememberSession: Boolean) {
        dataStore.edit {
            preferences -> preferences[Keys.REMEMBER_SESSION] = rememberSession
        }
    }

    suspend fun saveUserFullName(fullName: String) {
        dataStore.edit {
            preferences -> preferences[Keys.USER_FULL_NAME] = fullName
        }
    }

    suspend fun clearSession() {
        dataStore.edit {
            preferences -> preferences.clear()
        }
    }
}