package com.stopstone.musicplaylist.data.local.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationPreferences
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        private val dailyReminderEnabledKey = booleanPreferencesKey("daily_reminder_enabled")

        fun observeDailyReminderEnabled(): Flow<Boolean> =
            dataStore.data.map { preferences ->
                preferences[dailyReminderEnabledKey] ?: false
            }

        suspend fun setDailyReminderEnabled(enabled: Boolean) {
            dataStore.edit { preferences ->
                preferences[dailyReminderEnabledKey] = enabled
            }
        }

        suspend fun isDailyReminderEnabled(): Boolean {
            return observeDailyReminderEnabled().first()
        }
    }

