package com.stopstone.myapplication.data.api

import com.stopstone.myapplication.data.model.SearchResponse
import com.stopstone.myapplication.data.model.TokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SpotifyAuthApi {
    @POST("api/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Header("Authorization") auth: String,
        @Field("grant_type") grantType: String
    ): TokenResponse
}

interface SpotifyApi {
    @GET("v1/search")
    suspend fun searchTracks(
        @Header("Authorization") auth: String,
        @Query("q") query: String,
        @Query("type") type: String = "track"
    ): SearchResponse
}