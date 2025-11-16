package com.stopstone.musicplaylist.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stopstone.musicplaylist.ui.model.TrackUiState
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromEmotionStrings(emotions: List<String>): String {
        return Gson().toJson(emotions)
    }

    @TypeConverter
    fun toEmotionStrings(emotionsString: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(emotionsString, type)
    }

    @TypeConverter
    fun fromTrack(track: TrackUiState): String {
        return Gson().toJson(track)
    }

    @TypeConverter
    fun toTrack(trackString: String): TrackUiState {
        val type = object : TypeToken<TrackUiState>() {}.type
        return Gson().fromJson(trackString, type)
    }
}