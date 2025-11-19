package com.stopstone.musicplaylist.data.model.dto

import com.stopstone.musicplaylist.data.model.entity.DailyTrack
import com.stopstone.musicplaylist.ui.model.TrackUiState
import java.util.Date

/**
 * Firestore에 저장할 음악 정보 DTO
 * - Firestore는 커스텀 객체를 자동으로 직렬화/역직렬화 지원
 * - Date는 Timestamp로 자동 변환됨
 */
data class MusicDto(
    var musicId: String = "",
    var date: Date = Date(),
    var trackId: String = "",
    var imageUrl: String = "",
    var title: String = "",
    var artist: String = "",
    var emotions: List<String> = emptyList(),
    var comment: String? = null,
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var recordedAt: Date? = null, // 저장한 시각 정보
) {
    companion object {
        /**
         * DailyTrack을 MusicDto로 변환
         */
        fun fromDailyTrack(dailyTrack: DailyTrack): MusicDto =
            MusicDto(
                musicId = dailyTrack.id.toString(),
                date = dailyTrack.date,
                trackId = dailyTrack.track.id,
                imageUrl = dailyTrack.track.imageUrl,
                title = dailyTrack.track.title,
                artist = dailyTrack.track.artist,
                emotions = dailyTrack.emotions,
                comment = dailyTrack.comment,
                createdAt = dailyTrack.date,
                updatedAt = Date(),
                recordedAt = dailyTrack.recordedAt,
            )
    }

    /**
     * MusicDto를 DailyTrack으로 변환
     */
    fun toDailyTrack(): DailyTrack {
        // updatedAt이 recordedAt보다 나중이면 updatedAt 사용, 그렇지 않으면 recordedAt 사용
        val finalRecordedAt = when {
            updatedAt.after(recordedAt ?: Date(0)) -> updatedAt
            recordedAt != null -> recordedAt
            else -> null
        }
        return DailyTrack(
            id = musicId.toLongOrNull() ?: 0L,
            date = date,
            track =
                TrackUiState(
                    id = trackId,
                    imageUrl = imageUrl,
                    title = title,
                    artist = artist,
                ),
            emotions = emotions,
            comment = comment,
            recordedAt = finalRecordedAt,
        )
    }
}
