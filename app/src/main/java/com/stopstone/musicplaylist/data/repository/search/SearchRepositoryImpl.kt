package com.stopstone.musicplaylist.data.repository.search

import android.util.Log
import com.stopstone.musicplaylist.domain.model.Emotions
import com.stopstone.musicplaylist.data.remote.api.SpotifyApi
import com.stopstone.musicplaylist.data.model.response.Track
import com.stopstone.musicplaylist.domain.repository.search.SearchRepository
import java.util.Locale
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApi,
): SearchRepository {
    override suspend fun searchTracks(query: String): List<Track> = try {
        val (market, language) = getMarketLanguageByLocale()
        val response = spotifyApi.searchTracks(
            query = query,
            market = market,
            language = language
        )
        response.tracks.items
    } catch (e: Exception) {
        Log.e("SearchRepositoryImpl", "트랙 검색 중 오류 발생", e)
        emptyList()
    }

    override suspend fun getEmotions(): List<Emotions> {
        return Emotions.entries
    }

    private fun getMarketLanguageByLocale() = when (Locale.getDefault().language) {
        "ko" -> Pair("KR", "ko-KR")
        else -> Pair("US", "en-US")
    }
}