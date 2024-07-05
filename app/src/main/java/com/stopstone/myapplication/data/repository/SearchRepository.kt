package com.stopstone.myapplication.data.repository

import android.util.Base64
import android.util.Log
import com.stopstone.myapplication.BuildConfig
import com.stopstone.myapplication.data.api.SpotifyApi
import com.stopstone.myapplication.data.api.SpotifyAuthApi
import com.stopstone.myapplication.data.model.Track
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val spotifyAuthApi: SpotifyAuthApi,
    private val spotifyApi: SpotifyApi,
) {
    suspend fun searchTracks(query: String): List<Track> = try {
            val token = getAccessToken()
            val trackResponse = spotifyApi.searchTracks("Bearer $token", query)
            trackResponse.tracks.items
        } catch (e: Exception) {
            Log.e("SearchFragment", "Error: ${e.message}")
            emptyList() // 예외시 빈 리스트를 반환해야 함
        }

    private suspend fun getAccessToken(): String {
        val credentials = "${BuildConfig.CLIENT_ID}:${BuildConfig.CLIENT_SECRET}"
        val base64Credentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        val response = spotifyAuthApi.getToken(
            "Basic $base64Credentials",
            "client_credentials"
        )
        return response.accessToken
    }
}