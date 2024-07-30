package com.stopstone.myapplication.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val TOKEN_EXPIRE_TIME_KEY = longPreferencesKey("token_expire_time")

    fun getToken(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    fun getTokenExpireTime(): Flow<Long?> = dataStore.data.map { preferences ->
        preferences[TOKEN_EXPIRE_TIME_KEY]
    }

    suspend fun saveTokenExpireTime(expireTime: Long) {
        dataStore.edit { preferences ->
            preferences[TOKEN_EXPIRE_TIME_KEY] = expireTime
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}