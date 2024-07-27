package com.stopstone.myapplication.domain.usecase

import com.stopstone.myapplication.data.model.Track
import com.stopstone.myapplication.domain.repository.SearchRepository
import javax.inject.Inject

class SearchTracksUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): List<Track> {
        return repository.searchTracks(query)
    }
}