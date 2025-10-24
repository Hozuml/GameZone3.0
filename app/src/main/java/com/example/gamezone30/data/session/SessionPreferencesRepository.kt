package com.example.gamezone30.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. Creamos la instancia de DataStore para toda la app
val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session_preferences")

/**
 * Esta clase es el "cerebro" de DataStore.
 * Se encarga de guardar y leer datos simples (como un checkbox).
 */
class SessionPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    // 2. Definimos una "llave" para encontrar nuestro dato
    private object Keys {
        val REMEMBER_SESSION = booleanPreferencesKey("remember_session")
    }

    // 3. Creamos un "Flow" (flujo) que la app "escuchará"
    //    Nos dice si el usuario marcó "Recordar sesión" o no.
    val rememberSessionFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.REMEMBER_SESSION] ?: false // Si no existe, devuelve 'false'
    }

    // 4. Función para GUARDAR el estado del checkbox
    //    La llamamos "suspend" porque es una operación asíncrona.
    suspend fun setRememberSession(rememberSession: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.REMEMBER_SESSION] = rememberSession
        }
    }
}