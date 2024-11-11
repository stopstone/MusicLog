package com.stopstone.musicplaylist.data.repository.home

import com.stopstone.musicplaylist.data.model.entity.AudioFeatures
import com.stopstone.musicplaylist.data.model.response.Track
import com.stopstone.musicplaylist.data.remote.api.SpotifyApi
import com.stopstone.musicplaylist.domain.repository.home.RecommendRepository
import java.util.Locale
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
        val (market, language) = getMarketLanguageByLocale()

        val response = spotifyApi.getRecommendations(
            seedTracks = trackId,
            targetDanceability = audioFeatures.danceability,
            targetEnergy = audioFeatures.energy,
            targetValence = audioFeatures.valence,
            targetAcousticness = audioFeatures.acousticness,
            limit = limit,
            market = market,
            language = language,
        )
        return response.tracks
    }

    private fun getMarketLanguageByLocale() = when (Locale.getDefault().language) {
        "ko" -> Pair("KR", "ko-KR")
        else -> Pair("US", "en-US")
    }
}