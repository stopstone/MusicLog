package com.stopstone.musicplaylist.data.model.dto

import com.google.firebase.firestore.PropertyName
import com.stopstone.musicplaylist.data.model.entity.DailyTrack
import com.stopstone.musicplaylist.domain.model.Emotions
import com.stopstone.musicplaylist.ui.model.TrackUiState
import java.util.Date

/**
 * Firestore에 저장할 음악 정보 DTO
 * - Firestore는 커스텀 객체를 자동으로 직렬화/역직렬화 지원
 * - Date는 Timestamp로 자동 변환됨
 */
data class MusicDto(
    @PropertyName("music_id")
    val musicId: String = "",
    
    @PropertyName("date")
    val date: Date = Date(),
    
    @PropertyName("track_id")
    val trackId: String = "",
    
    @PropertyName("image_url")
    val imageUrl: String = "",
    
    @PropertyName("title")
    val title: String = "",
    
    @PropertyName("artist")
    val artist: String = "",
    
    @PropertyName("emotions")
    val emotions: List<String> = emptyList(),
    
    @PropertyName("comment")
    val comment: String? = null,
    
    @PropertyName("created_at")
    val createdAt: Date = Date(),
    
    @PropertyName("updated_at")
    val updatedAt: Date = Date()
) {
    companion object {
        /**
         * DailyTrack을 MusicDto로 변환
         */
        fun fromDailyTrack(dailyTrack: DailyTrack): MusicDto {
            return MusicDto(
                musicId = dailyTrack.id.toString(),
                date = dailyTrack.date,
                trackId = dailyTrack.track.id,
                imageUrl = dailyTrack.track.imageUrl,
                title = dailyTrack.track.title,
                artist = dailyTrack.track.artist,
                emotions = dailyTrack.emotions.map { it.name },
                comment = dailyTrack.comment,
                createdAt = dailyTrack.date,
                updatedAt = Date()
            )
        }
    }

    /**
     * MusicDto를 DailyTrack으로 변환
     */
    fun toDailyTrack(): DailyTrack {
        return DailyTrack(
            id = musicId.toLongOrNull() ?: 0L,
            date = date,
            track = TrackUiState(
                id = trackId,
                imageUrl = imageUrl,
                title = title,
                artist = artist
            ),
            emotions = emotions.mapNotNull { emotionName ->
                try {
                    Emotions.valueOf(emotionName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            },
            comment = comment
        )
    }
}
