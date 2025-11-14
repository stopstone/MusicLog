package com.stopstone.musicplaylist.domain.usecase.my

import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
import javax.inject.Inject

class GetMusicCountUseCase
    @Inject
    constructor(
        private val trackRepository: TrackRepository,
    ) {
        suspend operator fun invoke(): Int {
            return trackRepository.getMusicCount()
        }
    }

