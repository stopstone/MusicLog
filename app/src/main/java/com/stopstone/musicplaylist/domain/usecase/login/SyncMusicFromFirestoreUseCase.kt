package com.stopstone.musicplaylist.domain.usecase.login

import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
import javax.inject.Inject

class SyncMusicFromFirestoreUseCase @Inject constructor(
    private val trackRepository: TrackRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return trackRepository.syncFromFirestore(userId)
    }
}

