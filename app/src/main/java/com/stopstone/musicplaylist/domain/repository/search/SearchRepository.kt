package com.stopstone.musicplaylist.domain.repository.search

import com.stopstone.musicplaylist.data.model.Emotions
import com.stopstone.musicplaylist.data.model.response.Track

interface SearchRepository {
    suspend fun searchTracks(query: String): List<Track>
    suspend fun getEmotions(): List<Emotions>
}