package com.stopstone.myapplication.domain.usecase

import com.stopstone.myapplication.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class DeleteAllSearchesUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
){
    suspend operator fun invoke() {
        repository.deleteAllSearches()
    }
}