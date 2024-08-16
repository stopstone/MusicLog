package com.stopstone.myapplication.domain.usecase

import com.stopstone.myapplication.domain.repository.common.TrackRepository
import javax.inject.Inject

class DeleteAllTracksUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke() {
        repository.deleteAllTracks()
    }

}