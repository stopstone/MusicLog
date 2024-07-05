package com.stopstone.myapplication.data.model

data class SearchResponse(
    val tracks: Tracks
)

data class TokenResponse(val accessToken: String)