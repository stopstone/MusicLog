package com.stopstone.myapplication.domain.usecase.home

import com.stopstone.myapplication.data.model.response.Track
import com.stopstone.myapplication.data.model.response.Tracks
import com.stopstone.myapplication.domain.repository.home.RecommendRepository
import javax.inject.Inject

class GetRecommendationUseCase @Inject constructor(
    private val repository: RecommendRepository,
) {
    suspend operator fun invoke(trackId: String, limit: Int): List<Track> {
        val audioFeatures = repository.getAudioFeatures(trackId)
        return repository.getRecommendations(trackId, audioFeatures, limit)
    }
}