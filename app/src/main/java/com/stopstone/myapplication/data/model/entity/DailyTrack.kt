package com.stopstone.myapplication.data.model.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.stopstone.myapplication.data.model.Emotions
import com.stopstone.myapplication.ui.model.TrackUiState
import java.util.Date

@Entity(tableName = "daily_tracks", indices = [Index(value = ["date"], unique = true)])
data class DailyTrack(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val date: Date,
    val track: TrackUiState,
    val emotions: List<Emotions> = emptyList(),
    val comment: String? = null,
)