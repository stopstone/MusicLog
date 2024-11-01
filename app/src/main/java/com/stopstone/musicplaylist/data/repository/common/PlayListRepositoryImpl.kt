package com.stopstone.musicplaylist.data.repository.common

import com.stopstone.musicplaylist.data.local.dao.DailyTrackDao
import com.stopstone.musicplaylist.ui.model.TrackUiState
import com.stopstone.musicplaylist.domain.repository.common.PlayListRepository
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