package com.stopstone.myapplication.domain.usecase

import com.stopstone.myapplication.domain.repository.AuthRepository
import javax.inject.Inject

class ClearTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.clearTokenData()
    }
}