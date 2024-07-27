package com.stopstone.myapplication.domain.repository

import com.stopstone.myapplication.domain.model.TrackUiState

interface PlayListRepository {
    suspend fun getAllPlayList(): List<TrackUiState>
}