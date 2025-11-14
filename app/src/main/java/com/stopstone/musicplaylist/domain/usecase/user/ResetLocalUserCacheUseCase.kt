package com.stopstone.musicplaylist.domain.usecase.user

import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
import com.stopstone.musicplaylist.domain.repository.my.SignatureSongRepository
import javax.inject.Inject

class ResetLocalUserCacheUseCase
    @Inject
    constructor(
        private val trackRepository: TrackRepository,
        private val signatureSongRepository: SignatureSongRepository,
    ) {
        suspend operator fun invoke() {
            // 계정 전환과 로그아웃 시 로컬 캐시를 초기화
            trackRepository.clearLocalTracks()
            signatureSongRepository.clearLocalSignatureSongs()
        }
    }


