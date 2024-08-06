package com.stopstone.myapplication.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stopstone.myapplication.ui.model.TrackUiState
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
    fun fromTrack(track: TrackUiState): String {
        return Gson().toJson(track)
    }

    @TypeConverter
    fun toTrack(trackString: String): TrackUiState {
        val type = object : TypeToken<TrackUiState>() {}.type
        return Gson().fromJson(trackString, type)
    }
}