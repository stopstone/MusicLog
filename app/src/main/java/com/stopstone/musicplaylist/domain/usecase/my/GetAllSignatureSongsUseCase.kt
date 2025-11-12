package com.stopstone.musicplaylist.domain.usecase.my

import com.stopstone.musicplaylist.data.model.entity.SignatureSong
import com.stopstone.musicplaylist.domain.repository.my.SignatureSongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSignatureSongsUseCase
    @Inject
    constructor(
        private val signatureSongRepository: SignatureSongRepository,
    ) {
        operator fun invoke(): Flow<List<SignatureSong>> = signatureSongRepository.getAllSignatureSongs()
    }
