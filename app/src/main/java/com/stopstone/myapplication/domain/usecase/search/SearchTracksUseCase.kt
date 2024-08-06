package com.stopstone.myapplication.domain.usecase.search

import com.stopstone.myapplication.data.model.response.Track
import com.stopstone.myapplication.domain.repository.search.SearchRepository
import javax.inject.Inject

class SearchTracksUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String): List<Track> {
        return searchRepository.searchTracks(query)
    }
}