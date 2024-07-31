package com.stopstone.myapplication.data.model.response

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val tracks: Tracks
)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int
)
