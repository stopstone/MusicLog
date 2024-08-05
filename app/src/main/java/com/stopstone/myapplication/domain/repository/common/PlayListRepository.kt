package com.stopstone.myapplication.domain.repository.common

import com.stopstone.myapplication.domain.model.TrackUiState

interface PlayListRepository {
    suspend fun getAllPlayList(): List<TrackUiState>
}