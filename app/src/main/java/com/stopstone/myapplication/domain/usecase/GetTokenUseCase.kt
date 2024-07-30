package com.stopstone.myapplication.domain.usecase

import com.stopstone.myapplication.domain.repository.AuthRepository
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<String> {
        return repository.getToken()
    }
}