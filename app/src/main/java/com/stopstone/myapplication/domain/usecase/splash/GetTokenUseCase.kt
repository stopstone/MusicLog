package com.stopstone.myapplication.domain.usecase.splash

import com.stopstone.myapplication.domain.repository.common.AuthRepository
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): String {
        return repository.getToken()
    }
}