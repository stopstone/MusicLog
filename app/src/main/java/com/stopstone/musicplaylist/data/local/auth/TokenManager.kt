package com.stopstone.musicplaylist.data.local.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.stopstone.musicplaylist.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val tokenKey = stringPreferencesKey("token")
    private val tokenExpireTimeKey = longPreferencesKey("token_expire_time")

    fun getToken(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[tokenKey]
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    fun getTokenExpireTime(): Flow<Long?> = dataStore.data.map { preferences ->
        preferences[tokenExpireTimeKey]
    }

    suspend fun saveTokenExpireTime(expireTime: Long) {
        dataStore.edit { preferences ->
            preferences[tokenExpireTimeKey] = expireTime
        }
    }

    suspend fun isTokenExpired(): Boolean {
        val expireTime = getTokenExpireTime().first()
        return expireTime == null || DateUtils.getCurrentTimeMillis() >= expireTime
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}