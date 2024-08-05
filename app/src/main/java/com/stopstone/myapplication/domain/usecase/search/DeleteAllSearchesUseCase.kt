package com.stopstone.myapplication.domain.usecase.search

import com.stopstone.myapplication.domain.repository.search.SearchHistoryRepository
import javax.inject.Inject

class DeleteAllSearchesUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
){
    suspend operator fun invoke() {
        repository.deleteAllSearches()
    }
}