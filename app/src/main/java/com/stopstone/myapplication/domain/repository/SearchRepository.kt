package com.stopstone.myapplication.domain.repository

import com.stopstone.myapplication.data.model.Track

interface SearchRepository {
    suspend fun searchTracks(query: String): List<Track>
}