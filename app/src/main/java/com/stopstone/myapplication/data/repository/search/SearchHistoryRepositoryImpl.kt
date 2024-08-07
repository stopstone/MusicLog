package com.stopstone.myapplication.data.repository.search

import com.stopstone.myapplication.data.local.dao.SearchHistoryDao
import com.stopstone.myapplication.data.model.entity.SearchHistory
import com.stopstone.myapplication.domain.repository.search.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchHistoryRepositoryImpl @Inject constructor(
    private val recentSearchDao: SearchHistoryDao
) : SearchHistoryRepository {

    override suspend fun getAllSearchHistory(): Flow<List<SearchHistory>> =
        recentSearchDao.getSearchHistoryList()

    override suspend fun addSearch(query: String) {
        val existingSearch = recentSearchDao.findSearchByQuery(query)
        if (existingSearch != null) {
            recentSearchDao.updateSearch(existingSearch.copy(timestamp = System.currentTimeMillis()))
        } else {
            recentSearchDao.insertSearch(
                SearchHistory(
                    query = query,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun deleteSearch(search: SearchHistory) {
        recentSearchDao.deleteSearch(search)
    }

    override suspend fun deleteAllSearches() {
        recentSearchDao.deleteAllSearches()
    }
}