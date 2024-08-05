package com.stopstone.myapplication.domain.usecase.home

import com.stopstone.myapplication.data.model.entity.DailyTrack
import com.stopstone.myapplication.domain.repository.common.TrackRepository
import java.util.Date
import javax.inject.Inject

class GetTodayTrackUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(date: Date): DailyTrack? {
        return repository.getTodayTrack(date)
    }
}