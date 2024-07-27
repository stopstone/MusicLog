package com.stopstone.myapplication.domain.usecase

import com.stopstone.myapplication.data.model.DailyTrack
import com.stopstone.myapplication.domain.repository.TrackRepository
import java.util.Date
import javax.inject.Inject

class GetTodayTrackUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(date: Date): DailyTrack? {
        return repository.getTodayTrack(date)
    }
}