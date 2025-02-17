package com.ronik.geotraveljournal.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tokenDataStore by preferencesDataStore(name = "token_prefs")

object TokenManager {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    fun getTokenFlow(context: Context): Flow<String?> {
        return context.tokenDataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }
    }

    suspend fun saveToken(context: Context, token: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    suspend fun clearToken(context: Context) {
        context.tokenDataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }
}