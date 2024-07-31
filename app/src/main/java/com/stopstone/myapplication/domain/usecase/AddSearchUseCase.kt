package com.stopstone.myapplication.domain.usecase

import com.stopstone.myapplication.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class AddSearchUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(query: String) {
        repository.addSearch(query)
    }
}