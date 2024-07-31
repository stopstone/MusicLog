package com.stopstone.myapplication.domain.usecase

import com.stopstone.myapplication.data.model.SearchHistory
import com.stopstone.myapplication.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class DeleteSearchUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(search: SearchHistory) {
        repository.deleteSearch(search)
    }
}