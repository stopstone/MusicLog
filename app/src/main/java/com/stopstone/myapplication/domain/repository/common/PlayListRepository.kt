package com.stopstone.myapplication.domain.repository.common

import com.stopstone.myapplication.ui.model.TrackUiState

interface PlayListRepository {
    suspend fun getAllPlayList(): List<TrackUiState>
}