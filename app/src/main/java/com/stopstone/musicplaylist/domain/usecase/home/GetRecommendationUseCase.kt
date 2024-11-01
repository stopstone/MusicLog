package com.stopstone.musicplaylist.domain.usecase.home

import com.stopstone.musicplaylist.data.model.response.Track
import com.stopstone.musicplaylist.domain.repository.home.RecommendRepository
import javax.inject.Inject

class GetRecommendationUseCase @Inject constructor(
    private val repository: RecommendRepository,
) {
    suspend operator fun invoke(trackId: String, limit: Int): List<Track> {
        val audioFeatures = repository.getAudioFeatures(trackId)
        return repository.getRecommendations(trackId, audioFeatures, limit)
    }
}