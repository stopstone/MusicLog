package com.stopstone.myapplication.domain.repository

import com.stopstone.myapplication.data.model.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    suspend fun getAllSearchHistory(): Flow<List<SearchHistory>>
    suspend fun addSearch(query: String)
    suspend fun deleteSearch(search: SearchHistory)
    suspend fun deleteAllSearches()
}