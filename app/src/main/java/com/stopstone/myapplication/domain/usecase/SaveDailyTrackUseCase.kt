package com.stopstone.myapplication.domain.usecase

import com.stopstone.myapplication.domain.model.TrackUiState
import com.stopstone.myapplication.domain.repository.TrackRepository
import java.util.Date
import javax.inject.Inject

class SaveDailyTrackUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(track: TrackUiState, date: Date) {
        repository.saveDailyTrack(track, date)
    }
}