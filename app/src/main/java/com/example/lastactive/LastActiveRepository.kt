package com.example.lastactive

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class LastActiveRepository(private val context: Context) {
    companion object {
        private val LAST_ACTIVE_KEY = longPreferencesKey("last_active")
    }

    val lastActiveFlow: Flow<Long?> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[LAST_ACTIVE_KEY]
            }

    suspend fun saveLastActive(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_ACTIVE_KEY] = timestamp
        }
    }
}
