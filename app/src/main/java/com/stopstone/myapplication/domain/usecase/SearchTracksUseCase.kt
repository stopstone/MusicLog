package com.stopstone.myapplication.domain.usecase

import android.util.Log
import com.stopstone.myapplication.data.model.response.Track
import com.stopstone.myapplication.domain.repository.AuthRepository
import com.stopstone.myapplication.domain.repository.SearchRepository
import javax.inject.Inject

class SearchTracksUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<Track>> = runCatching {
        authRepository.getToken().getOrThrow()
        searchRepository.searchTracks(query)
    }.onFailure { e ->
        Log.e("SearchTracksUseCase", "Error searching tracks", e)
    }
}