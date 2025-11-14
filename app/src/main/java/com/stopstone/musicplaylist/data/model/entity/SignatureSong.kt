package com.stopstone.musicplaylist.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stopstone.musicplaylist.ui.model.TrackUiState
import java.util.Date

@Entity(tableName = "signature_song")
data class SignatureSong(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val track: TrackUiState,
    val selectedAt: Date = Date(),
    val isActive: Boolean = true,
)
