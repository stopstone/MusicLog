package com.stopstone.musicplaylist.data.repository.home

import com.stopstone.musicplaylist.data.model.entity.AudioFeatures
import com.stopstone.musicplaylist.data.model.response.Track
import com.stopstone.musicplaylist.data.remote.api.SpotifyApi
import com.stopstone.musicplaylist.domain.repository.home.RecommendRepository
import javax.inject.Inject

class RecommendRepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApi
): RecommendRepository {
    override suspend fun getAudioFeatures(trackId: String): AudioFeatures {
        return spotifyApi.getAudioFeatures(trackId)
    }

    override suspend fun getRecommendations(
        trackId: String,
        audioFeatures: AudioFeatures,
        limit: Int
    ): List<Track> {
        val response = spotifyApi.getRecommendations(
            seedTracks = trackId,
            targetDanceability = audioFeatures.danceability,
            targetEnergy = audioFeatures.energy,
            targetValence = audioFeatures.valence,
            targetAcousticness = audioFeatures.acousticness,
            limit = limit
        )
        return response.tracks
    }
}