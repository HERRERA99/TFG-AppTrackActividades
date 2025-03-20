package com.aitor.trackactividades.core.userPreferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.aitor.trackactividades.authentication.data.response.UserResponse
import com.aitor.trackactividades.core.model.Gender
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore: DataStore<Preferences> = context.dataStore

    companion object {
        val ID_KEY = intPreferencesKey("id")
        val USERNAME_KEY = stringPreferencesKey("username")
        val NOMBRE_KEY = stringPreferencesKey("nombre")
        val APELLIDOS_KEY = stringPreferencesKey("apellidos")
        val EMAIL_KEY = stringPreferencesKey("email")
        val PESO_KEY = doublePreferencesKey("peso")
        val ALTURA_KEY = intPreferencesKey("altura")
        val GENERO_KEY = stringPreferencesKey("genero")
    }

    val userFlow: Flow<UserResponse?> = dataStore.data.map { preferences ->
        val id = preferences[ID_KEY] ?: return@map null
        UserResponse(
            id = id,
            username = preferences[USERNAME_KEY] ?: "",
            nombre = preferences[NOMBRE_KEY] ?: "",
            apellidos = preferences[APELLIDOS_KEY] ?: "",
            email = preferences[EMAIL_KEY] ?: "",
            peso = preferences[PESO_KEY] ?: 0.0,
            altura = preferences[ALTURA_KEY] ?: 0,
            genero = try {
                Gender.valueOf(preferences[GENERO_KEY] ?: "NO_DEFINIDO")
            } catch (e: IllegalArgumentException) {
                Gender.OTRO // Si hay un valor inválido, usa un valor por defecto
            }
        )
    }

    suspend fun saveUser(user: UserResponse) {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = user.id
            preferences[USERNAME_KEY] = user.username
            preferences[NOMBRE_KEY] = user.nombre
            preferences[APELLIDOS_KEY] = user.apellidos
            preferences[EMAIL_KEY] = user.email
            preferences[PESO_KEY] = user.peso
            preferences[ALTURA_KEY] = user.altura
            preferences[GENERO_KEY] = user.genero.name
        }
    }

    suspend fun clearUser() {
        dataStore.edit { it.clear() }
    }
}

