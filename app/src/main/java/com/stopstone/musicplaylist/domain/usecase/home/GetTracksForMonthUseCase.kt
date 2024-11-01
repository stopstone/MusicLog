package com.stopstone.musicplaylist.domain.usecase.home

import com.stopstone.musicplaylist.data.model.entity.DailyTrack
import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
import javax.inject.Inject

class GetTracksForMonthUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(year: Int, month: Int): List<DailyTrack> {
        return repository.getTracksForMonth(year, month)
    }
}