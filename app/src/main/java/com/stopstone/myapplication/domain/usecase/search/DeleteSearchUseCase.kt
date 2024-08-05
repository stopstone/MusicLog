package com.stopstone.myapplication.domain.usecase.search

import com.stopstone.myapplication.data.model.entity.SearchHistory
import com.stopstone.myapplication.domain.repository.search.SearchHistoryRepository
import javax.inject.Inject

class DeleteSearchUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(search: SearchHistory) {
        repository.deleteSearch(search)
    }
}