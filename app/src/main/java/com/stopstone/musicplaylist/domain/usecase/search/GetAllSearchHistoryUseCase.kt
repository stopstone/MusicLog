package com.stopstone.musicplaylist.domain.usecase.search

import com.stopstone.musicplaylist.data.model.entity.SearchHistory
import com.stopstone.musicplaylist.domain.repository.search.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(): Flow<List<SearchHistory>> =
        repository.getAllSearchHistory()
}