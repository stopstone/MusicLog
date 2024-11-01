package com.stopstone.musicplaylist.domain.usecase.search

import com.stopstone.musicplaylist.domain.repository.search.SearchHistoryRepository
import javax.inject.Inject

class AddSearchUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(query: String) {
        repository.addSearch(query)
    }
}