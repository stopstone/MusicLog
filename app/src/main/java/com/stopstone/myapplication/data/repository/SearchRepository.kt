package com.stopstone.myapplication.data.repository

import android.util.Base64
import android.util.Log
import com.stopstone.myapplication.BuildConfig
import com.stopstone.myapplication.data.api.RetrofitClient
import com.stopstone.myapplication.data.model.Track

class SearchRepository {
    suspend fun searchTracks(query: String): List<Track> {
        return try {
            val token = getAccessToken()
            val trackResponse = RetrofitClient.spotifyApi.searchTracks("Bearer $token", query)
            trackResponse.tracks.items
        } catch (e: Exception) {
            Log.e("SearchFragment", "Error: ${e.message}")
            emptyList() // 예외시 빈 리스트를 반환해야 함
        }
    }

    private suspend fun getAccessToken(): String {
        val credentials = "${BuildConfig.CLIENT_ID}:${BuildConfig.CLIENT_SECRET}"
        val base64Credentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        val response = RetrofitClient.spotifyAuthApi.getToken(
            "Basic $base64Credentials",
            "client_credentials"
        )
        return response.access_token
    }
}