package com.stopstone.myapplication.data.repository

import android.util.Base64
import android.util.Log
import com.stopstone.myapplication.BuildConfig
import com.stopstone.myapplication.data.api.SpotifyApi
import com.stopstone.myapplication.data.api.SpotifyAuthApi
import com.stopstone.myapplication.data.model.TokenResponse
import com.stopstone.myapplication.data.model.Track
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val spotifyAuthApi: SpotifyAuthApi,
    private val spotifyApi: SpotifyApi,
) {
    private var cachedToken: String? = null
    private var tokenExpirationTime: Long = 0

    suspend fun searchTracks(query: String): List<Track> = try {
        val token = getValidAccessToken()
        val trackResponse = spotifyApi.searchTracks("Bearer $token", query)
        trackResponse.tracks.items
    } catch (e: Exception) {
        Log.e("SearchRepository", "Error searching tracks: ${e.message}")
        emptyList()
    }

    private suspend fun getValidAccessToken(): String {
        val currentTime = System.currentTimeMillis()
        if (cachedToken == null || currentTime >= tokenExpirationTime) {
            val tokenResponse = getAccessToken()
            cachedToken = tokenResponse.accessToken
            tokenExpirationTime = currentTime + (tokenResponse.expiresIn * 1000)
        }
        return cachedToken!!
    }

    private suspend fun getAccessToken(): TokenResponse {
        val credentials = "${BuildConfig.CLIENT_ID}:${BuildConfig.CLIENT_SECRET}"
        val base64Credentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        val response = spotifyAuthApi.getToken(
            "Basic $base64Credentials",
            "client_credentials"
        )
        return response
    }
}