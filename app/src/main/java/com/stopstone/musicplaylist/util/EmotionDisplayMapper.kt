package com.stopstone.musicplaylist.util

import android.content.Context
import com.stopstone.musicplaylist.domain.model.Emotions

object EmotionDisplayMapper {
    fun mapToDisplayNames(
        context: Context,
        emotionNames: List<String>,
    ): List<String> {
        return emotionNames.map { mapSingleToDisplayName(context, it) }
    }

    fun mapSingleToDisplayName(
        context: Context,
        emotionName: String,
    ): String {
        return try {
            val enumValue = Emotions.valueOf(emotionName)
            enumValue.getDisplayName(context)
        } catch (e: IllegalArgumentException) {
            emotionName
        }
    }
}

