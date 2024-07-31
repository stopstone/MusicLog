package com.stopstone.myapplication.data.repository

import com.stopstone.myapplication.data.local.dao.DailyTrackDao
import com.stopstone.myapplication.domain.model.TrackUiState
import com.stopstone.myapplication.domain.repository.PlayListRepository
import javax.inject.Inject

class PlayListRepositoryImpl @Inject constructor(
    private val dailyTrackDao: DailyTrackDao
) : PlayListRepository {

    override suspend fun getAllPlayList(): List<TrackUiState> {
        return dailyTrackDao.getAllTracks().map {
            it.track
        }
    }
}