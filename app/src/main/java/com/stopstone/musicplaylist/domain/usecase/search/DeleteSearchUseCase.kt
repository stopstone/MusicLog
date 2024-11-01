package com.stopstone.musicplaylist.domain.usecase.search

import com.stopstone.musicplaylist.data.model.entity.SearchHistory
import com.stopstone.musicplaylist.domain.repository.search.SearchHistoryRepository
import javax.inject.Inject

class DeleteSearchUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(search: SearchHistory) {
        repository.deleteSearch(search)
    }
}