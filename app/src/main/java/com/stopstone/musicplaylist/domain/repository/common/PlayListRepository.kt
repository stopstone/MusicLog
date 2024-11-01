package com.stopstone.musicplaylist.domain.repository.common

import com.stopstone.musicplaylist.ui.model.TrackUiState

interface PlayListRepository {
    suspend fun getAllPlayList(): List<TrackUiState>
}