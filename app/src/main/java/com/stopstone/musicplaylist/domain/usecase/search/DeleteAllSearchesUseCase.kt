package com.stopstone.musicplaylist.domain.usecase.search

import com.stopstone.musicplaylist.domain.repository.search.SearchHistoryRepository
import javax.inject.Inject

class DeleteAllSearchesUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
){
    suspend operator fun invoke() {
        repository.deleteAllSearches()
    }
}