package com.stopstone.musicplaylist.domain.usecase.search

import com.stopstone.musicplaylist.data.model.entity.DailyTrack
import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
import com.stopstone.musicplaylist.ui.model.TrackUiState
import java.util.Date
import javax.inject.Inject

class SaveDailyTrackUseCase
    @Inject
    constructor(
        private val repository: TrackRepository,
    ) {
        suspend operator fun invoke(
            track: TrackUiState,
            emotions: List<String>,
            date: Date,
            comment: String? = null,
        ) {
            val dailyTrack =
                DailyTrack(
                    date = date,
                    track = track,
                    emotions = emotions,
                    comment = comment,
                )
            repository.saveDailyTrack(dailyTrack)
        }
    }
