package com.stopstone.musicplaylist.data.local.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstagramSharePreferences
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        private val showEmotionsKey = booleanPreferencesKey("show_emotions")
        private val showMemoKey = booleanPreferencesKey("show_memo")

        fun getShowEmotions(): Flow<Boolean> =
            dataStore.data.map { preferences ->
                preferences[showEmotionsKey] ?: false
            }

        suspend fun setShowEmotions(show: Boolean) {
            dataStore.edit { preferences ->
                preferences[showEmotionsKey] = show
            }
        }

        fun getShowMemo(): Flow<Boolean> =
            dataStore.data.map { preferences ->
                preferences[showMemoKey] ?: false
            }

        suspend fun setShowMemo(show: Boolean) {
            dataStore.edit { preferences ->
                preferences[showMemoKey] = show
            }
        }
    }

