package com.stopstone.myapplication.domain.usecase.search

import com.stopstone.myapplication.domain.repository.search.SearchHistoryRepository
import javax.inject.Inject

class AddSearchUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(query: String) {
        repository.addSearch(query)
    }
}