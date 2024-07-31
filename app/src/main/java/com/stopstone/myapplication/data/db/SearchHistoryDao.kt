package com.stopstone.myapplication.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.stopstone.myapplication.data.model.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 5")
    fun getSearchHistoryList(): Flow<List<SearchHistory>>

    @Query("SELECT * FROM search_history WHERE query = :query LIMIT 1")
    suspend fun findSearchByQuery(query: String): SearchHistory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistory)

    @Update
    suspend fun updateSearch(search: SearchHistory)

    @Delete
    suspend fun deleteSearch(search: SearchHistory)

    @Query("DELETE FROM search_history")
    suspend fun deleteAllSearches()
}