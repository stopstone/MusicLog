package com.stopstone.myapplication.domain.repository

import com.stopstone.myapplication.data.model.entity.DailyTrack
import com.stopstone.myapplication.domain.model.TrackUiState
import java.util.Date

interface TrackRepository {
    suspend fun saveDailyTrack(track: TrackUiState, date: Date)
    suspend fun getTodayTrack(date: Date): DailyTrack?
    suspend fun getTracksForMonth(year: Int, month: Int): List<DailyTrack>
    suspend fun getComment(date: Date): String?
    suspend fun updateComment(date: Date, comment: String)
}