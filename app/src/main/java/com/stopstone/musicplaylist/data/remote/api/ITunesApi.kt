package com.stopstone.musicplaylist.data.remote.api

import com.stopstone.musicplaylist.data.model.response.ITunesSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search")
    suspend fun searchTracks(
        @Query("term") term: String,
        @Query("country") country: String,
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("entity") entry: String = ENTITY_SONG,
        @Query("media") media: String = MEDIA_MUSIC,
    ): ITunesSearchResponse

    companion object {
        private const val DEFAULT_LIMIT = 20
        private const val ENTITY_SONG = "song"
        private const val MEDIA_MUSIC = "music"
    }
}
