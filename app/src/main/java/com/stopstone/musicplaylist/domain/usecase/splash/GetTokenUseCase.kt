package com.stopstone.musicplaylist.domain.usecase.splash

import com.stopstone.musicplaylist.domain.repository.common.AuthRepository
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): String {
        return repository.getToken()
    }
}