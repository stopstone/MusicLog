package com.stopstone.musicplaylist.domain.usecase.my

import com.stopstone.musicplaylist.data.model.entity.SignatureSong
import com.stopstone.musicplaylist.domain.repository.my.SignatureSongRepository
import javax.inject.Inject

class SetSignatureSongUseCase
    @Inject
    constructor(
        private val signatureSongRepository: SignatureSongRepository,
    ) {
        suspend operator fun invoke(signatureSong: SignatureSong) {
            signatureSongRepository.setSignatureSong(signatureSong)
        }
    }
