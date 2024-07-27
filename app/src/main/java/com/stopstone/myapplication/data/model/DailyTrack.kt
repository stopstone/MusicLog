package com.stopstone.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stopstone.myapplication.domain.model.TrackUiState
import java.util.Date

@Entity(tableName = "daily_tracks")
data class DailyTrack(
    @PrimaryKey val date: Date,
    val track: TrackUiState
)