package com.stopstone.musicplaylist.data.remote.dto

import com.google.firebase.firestore.DocumentSnapshot
import com.stopstone.musicplaylist.data.model.entity.SignatureSong
import com.stopstone.musicplaylist.ui.model.TrackUiState
import java.util.Date

data class SignatureSongRemoteDto(
    val track: TrackRemoteDto,
    val selectedAt: Date,
    val isActive: Boolean,
) {
    fun toFirestoreMap(): Map<String, Any?> =
        mapOf(
            FIELD_TRACK to track.toFirestoreMap(),
            FIELD_SELECTED_AT to selectedAt,
            FIELD_IS_ACTIVE to isActive,
        )

    companion object {
        private const val FIELD_TRACK = "track"
        private const val FIELD_SELECTED_AT = "selected_at"
        private const val FIELD_IS_ACTIVE = "is_active"

        fun fromDocument(document: DocumentSnapshot): SignatureSongRemoteDto? {
            val trackMap = document.get(FIELD_TRACK) as? Map<*, *> ?: return null
            val trackDto = TrackRemoteDto.fromMap(trackMap) ?: return null
            val selectedAt = document.getDate(FIELD_SELECTED_AT) ?: Date()
            val isActive = document.getBoolean(FIELD_IS_ACTIVE) ?: true
            return SignatureSongRemoteDto(
                track = trackDto,
                selectedAt = selectedAt,
                isActive = isActive,
            )
        }
    }
}

data class TrackRemoteDto(
    val id: String,
    val imageUrl: String,
    val title: String,
    val artist: String,
) {
    fun toFirestoreMap(): Map<String, Any?> =
        mapOf(
            FIELD_ID to id,
            FIELD_IMAGE_URL to imageUrl,
            FIELD_TITLE to title,
            FIELD_ARTIST to artist,
        )

    companion object {
        private const val FIELD_ID = "id"
        private const val FIELD_IMAGE_URL = "image_url"
        private const val FIELD_TITLE = "title"
        private const val FIELD_ARTIST = "artist"

        fun fromMap(map: Map<*, *>?): TrackRemoteDto? {
            val id = map?.get(FIELD_ID) as? String ?: return null
            val imageUrl = map[FIELD_IMAGE_URL] as? String ?: ""
            val title = map[FIELD_TITLE] as? String ?: ""
            val artist = map[FIELD_ARTIST] as? String ?: ""
            return TrackRemoteDto(
                id = id,
                imageUrl = imageUrl,
                title = title,
                artist = artist,
            )
        }
    }
}

fun SignatureSong.toRemoteDto(): SignatureSongRemoteDto =
    SignatureSongRemoteDto(
        track =
            TrackRemoteDto(
                id = track.id,
                imageUrl = track.imageUrl,
                title = track.title,
                artist = track.artist,
            ),
        selectedAt = selectedAt,
        isActive = isActive,
    )

fun SignatureSongRemoteDto.toEntity(): SignatureSong =
    SignatureSong(
        track =
            TrackUiState(
                id = track.id,
                imageUrl = track.imageUrl,
                title = track.title,
                artist = track.artist,
            ),
        selectedAt = selectedAt,
        isActive = isActive,
    )
