package com.stopstone.musicplaylist.domain.usecase.detail

import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
import java.util.Date
import javax.inject.Inject

class DeleteTrackUseCase @Inject constructor(private val repository: TrackRepository) {
    suspend operator fun invoke(dateMillis: Date) {
        repository.deleteTrackByDate(dateMillis)
    }
}