package com.stopstone.musicplaylist.data.local.settings.model

data class EmotionSettingPreferences(
    val customEmotions: Set<String>,
    val emotionOrder: List<String>,
    val hiddenEmotions: Set<String>,
)
