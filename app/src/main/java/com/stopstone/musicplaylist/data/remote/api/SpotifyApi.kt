package com.stopstone.musicplaylist.data.remote.api

import com.stopstone.musicplaylist.data.model.entity.AudioFeatures
import com.stopstone.musicplaylist.data.model.response.RecommendationsResponse
import com.stopstone.musicplaylist.data.model.response.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApi {
    @GET("v1/search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("type") type: String = "track"
    ): SearchResponse

    @GET("v1/audio-features/{id}")
    suspend fun getAudioFeatures(@Path("id") trackId: String): AudioFeatures

    @GET("v1/recommendations")
    suspend fun getRecommendations(
        @Query("seed_tracks") seedTracks: String,
        @Query("target_danceability") targetDanceability: Float,
        @Query("target_energy") targetEnergy: Float,
        @Query("target_valence") targetValence: Float,
        @Query("target_acousticness") targetAcousticness: Float,
        @Query("limit") limit: Int
    ): RecommendationsResponse
}