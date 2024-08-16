package com.stopstone.myapplication.domain.usecase.search

import com.stopstone.myapplication.data.model.Emotions
import com.stopstone.myapplication.data.model.entity.DailyTrack
import com.stopstone.myapplication.ui.model.TrackUiState
import com.stopstone.myapplication.domain.repository.common.TrackRepository
import java.util.Date
import javax.inject.Inject

class SaveDailyTrackUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(track: TrackUiState, emotions: List<Emotions>, date: Date) {
        val dailyTrack = DailyTrack(
            date = date,
            track = track,
            emotions = emotions
        )
        repository.saveDailyTrack(dailyTrack)
    }
}