package com.stopstone.myapplication.domain.usecase

import com.stopstone.myapplication.data.model.SearchHistory
import com.stopstone.myapplication.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(): Flow<List<SearchHistory>> =
        repository.getAllSearchHistory()
}