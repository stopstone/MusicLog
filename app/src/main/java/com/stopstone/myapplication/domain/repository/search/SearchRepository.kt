package com.stopstone.myapplication.domain.repository.search

import com.stopstone.myapplication.data.model.Emotions
import com.stopstone.myapplication.data.model.response.Track

interface SearchRepository {
    suspend fun searchTracks(query: String): List<Track>
    suspend fun getEmotions(): List<Emotions>
}