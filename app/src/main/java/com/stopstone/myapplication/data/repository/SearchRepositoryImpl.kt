package com.stopstone.myapplication.data.repository

import android.util.Log
import com.stopstone.myapplication.data.remote.api.SpotifyApi
import com.stopstone.myapplication.data.model.response.Track
import com.stopstone.myapplication.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApi,
) : SearchRepository {

    override suspend fun searchTracks(query: String): List<Track> {
        Log.d("SearchRepositoryImpl", "쿼리로 트랙 검색 중: $query")

        return try {
            val response = spotifyApi.searchTracks(query)
            Log.d("SearchRepositoryImpl", "응답: $response")
            response.tracks.items
        } catch (e: Exception) {
            Log.e("SearchRepositoryImpl", "트랙 검색 중 오류 발생", e)
            emptyList()
        }
    }
}
