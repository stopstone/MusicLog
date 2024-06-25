package com.stopstone.myapplication.data.model

data class SearchResponse(
    val tracks: Tracks
)

data class TokenResponse(val access_token: String)