package com.stopstone.myapplication.data.api

import com.stopstone.myapplication.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyApi {
    @GET("v1/search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("type") type: String = "track"
    ): SearchResponse
}