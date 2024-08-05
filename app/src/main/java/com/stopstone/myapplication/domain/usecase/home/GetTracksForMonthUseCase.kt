package com.stopstone.myapplication.domain.usecase.home

import com.stopstone.myapplication.data.model.entity.DailyTrack
import com.stopstone.myapplication.domain.repository.common.TrackRepository
import javax.inject.Inject

class GetTracksForMonthUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(year: Int, month: Int): List<DailyTrack> {
        return repository.getTracksForMonth(year, month)
    }
}