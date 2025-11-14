package com.stopstone.musicplaylist.data.remote.dto

data class EmotionSettingDto(
    val customEmotionNames: List<String> = emptyList(),
    val orderedEmotionIds: List<String> = emptyList(),
    val hiddenEmotionIds: List<String> = emptyList(),
)
