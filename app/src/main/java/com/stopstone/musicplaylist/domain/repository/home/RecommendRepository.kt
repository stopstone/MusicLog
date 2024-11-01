package com.stopstone.musicplaylist.domain.repository.home

import com.stopstone.musicplaylist.data.model.entity.AudioFeatures
import com.stopstone.musicplaylist.data.model.response.Track

interface RecommendRepository {
    suspend fun getAudioFeatures(trackId: String): AudioFeatures
    suspend fun getRecommendations(
        trackId: String,
        audioFeatures: AudioFeatures,
        limit: Int
    ): List<Track>
}