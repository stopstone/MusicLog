package com.stopstone.musicplaylist.data.model.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.stopstone.musicplaylist.ui.model.TrackUiState
import java.util.Date

@Entity(tableName = "daily_tracks", indices = [Index(value = ["date"], unique = true)])
data class DailyTrack(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val date: Date,
    val track: TrackUiState,
    val emotions: List<String> = emptyList(),
    val comment: String? = null,
    val recordedAt: Date? = null,
)
