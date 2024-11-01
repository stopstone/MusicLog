package com.stopstone.musicplaylist.data.repository.search

import android.util.Log
import com.stopstone.musicplaylist.data.model.Emotions
import com.stopstone.musicplaylist.data.remote.api.SpotifyApi
import com.stopstone.musicplaylist.data.model.response.Track
import com.stopstone.musicplaylist.domain.repository.search.SearchRepository
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

    override suspend fun getEmotions(): List<Emotions> {
        return Emotions.entries
    }
}
