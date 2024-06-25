package com.stopstone.myapplication.ui.viewmodel

import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.BuildConfig
import com.stopstone.myapplication.data.api.RetrofitClient
import com.stopstone.myapplication.data.model.Track
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    fun searchTracks(query: String) = viewModelScope.launch {
        try {
            val token = getAccessToken()
            val trackResponse = RetrofitClient.spotifyApi.searchTracks("Bearer $token", query)
            _tracks.value = trackResponse.tracks.items
        } catch (e: Exception) {
            Log.e("SearchFragment", "Error: ${e.message}")
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