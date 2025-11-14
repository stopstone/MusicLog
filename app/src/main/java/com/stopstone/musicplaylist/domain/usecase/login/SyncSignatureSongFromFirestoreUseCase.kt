package com.stopstone.musicplaylist.domain.usecase.login

import com.stopstone.musicplaylist.domain.repository.my.SignatureSongRepository
import javax.inject.Inject

class SyncSignatureSongFromFirestoreUseCase
    @Inject
    constructor(
        private val signatureSongRepository: SignatureSongRepository,
    ) {
        suspend operator fun invoke(userId: String): Result<Unit> = signatureSongRepository.syncFromFirestore(userId)
    }
