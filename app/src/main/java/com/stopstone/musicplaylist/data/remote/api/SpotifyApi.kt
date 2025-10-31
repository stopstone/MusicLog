package com.stopstone.musicplaylist.data.remote.api

import com.stopstone.musicplaylist.data.model.response.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyApi {
    @GET("v1/search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("market") market: String,
        @Header("Accept-Language") language: String
    ): SearchResponse

}