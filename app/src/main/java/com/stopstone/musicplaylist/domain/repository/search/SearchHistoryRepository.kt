package com.stopstone.musicplaylist.domain.repository.search

import com.stopstone.musicplaylist.data.model.entity.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    suspend fun getAllSearchHistory(): Flow<List<SearchHistory>>
    suspend fun addSearch(query: String)
    suspend fun deleteSearch(search: SearchHistory)
    suspend fun deleteAllSearches()
}