package com.stopstone.musicplaylist.domain.usecase

import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
import javax.inject.Inject

class DeleteAllTracksUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke() {
        repository.deleteAllTracks()
    }

}