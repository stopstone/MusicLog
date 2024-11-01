package com.stopstone.musicplaylist.data.remote.api

import com.stopstone.musicplaylist.data.model.response.TokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SpotifyAuthApi {
    @POST("api/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): TokenResponse
}