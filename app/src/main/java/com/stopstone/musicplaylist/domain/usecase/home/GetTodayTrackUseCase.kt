package com.stopstone.musicplaylist.domain.usecase.home

import com.stopstone.musicplaylist.data.model.entity.DailyTrack
import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
import java.util.Date
import javax.inject.Inject

class GetTodayTrackUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(date: Date): DailyTrack? {
        return repository.getTodayTrack(date)
    }
}