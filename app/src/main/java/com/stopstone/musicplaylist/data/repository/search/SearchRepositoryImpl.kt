package com.stopstone.musicplaylist.data.repository.search

import android.util.Log
import com.stopstone.musicplaylist.data.model.dto.toTrack
import com.stopstone.musicplaylist.data.model.response.Track
import com.stopstone.musicplaylist.data.remote.api.ITunesApi
import com.stopstone.musicplaylist.domain.model.Emotions
import com.stopstone.musicplaylist.domain.repository.search.SearchRepository
import java.util.Locale
import javax.inject.Inject

class SearchRepositoryImpl
    @Inject
    constructor(
        private val iTunesApi: ITunesApi,
    ) : SearchRepository {
        override suspend fun searchTracks(query: String): List<Track> =
            try {
                val response =
                    iTunesApi.searchTracks(
                        term = query,
                        country = resolveCountry(),
                    )
                response.results.map { it.toTrack() }
            } catch (e: Exception) {
                Log.e("SearchRepositoryImpl", "트랙 검색 중 오류 발생", e)
                emptyList()
            }

        override suspend fun getEmotions(): List<Emotions> = Emotions.entries

        private fun resolveCountry(): String =
            when (Locale.getDefault().language) {
                LANGUAGE_KOREAN -> COUNTRY_KOREA
                else -> COUNTRY_US
            }

        companion object {
            private const val TAG = "SearchRepositoryImpl"
            private const val LANGUAGE_KOREAN = "ko"
            private const val COUNTRY_KOREA = "KR"
            private const val COUNTRY_US = "US"
        }
    }
