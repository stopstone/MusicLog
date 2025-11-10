package com.stopstone.musicplaylist.data.local.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 사용자 정보를 관리하는 클래스
 * - userId 저장/로드
 */
@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val userIdKey = stringPreferencesKey("user_id")

    // 사용자 ID 가져오기
    fun getUserId(): Flow<String?> = dataStore.data.map { preferences ->
        val userId = preferences[userIdKey]
        userId
    }

    // 사용자 ID 가져오기
    suspend fun getUserIdSync(): String? {
        val userId = getUserId().first()
        return userId
    }

    // 사용자 ID 저장
    suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[userIdKey] = userId
        }
    }

    // 사용자 ID 삭제
    suspend fun clearUserId() {
        dataStore.edit { preferences ->
            preferences.remove(userIdKey)
        }
    }
}

