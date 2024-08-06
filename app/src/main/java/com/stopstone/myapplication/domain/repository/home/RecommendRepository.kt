package com.stopstone.myapplication.domain.repository.home

import com.stopstone.myapplication.data.model.entity.AudioFeatures
import com.stopstone.myapplication.data.model.response.Track

interface RecommendRepository {
    suspend fun getAudioFeatures(trackId: String): AudioFeatures
    suspend fun getRecommendations(
        trackId: String,
        audioFeatures: AudioFeatures,
        limit: Int
    ): List<Track>
}