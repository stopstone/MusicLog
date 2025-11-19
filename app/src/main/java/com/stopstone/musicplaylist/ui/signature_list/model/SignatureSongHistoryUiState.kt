package com.stopstone.musicplaylist.ui.signature_list.model

import com.stopstone.musicplaylist.base.BaseIdModel
import com.stopstone.musicplaylist.data.model.entity.SignatureSong
import com.stopstone.musicplaylist.util.DateUtils
import java.util.Date

data class SignatureSongHistoryUiState(
    val historyId: Long,
    val title: String,
    val artist: String,
    val imageUrl: String,
    val selectedAt: Date,
    val daysSinceSelected: Int,
    val isActive: Boolean,
) : BaseIdModel {
    override val id: Any
        get() = historyId
}

data class SignatureListUiState(
    val isLoading: Boolean = true,
    val songs: List<SignatureSongHistoryUiState> = emptyList(),
    val errorMessage: String? = null,
)

fun SignatureSong.toHistoryUiState(baseDate: Date): SignatureSongHistoryUiState {
    val identifier = if (id == 0L) selectedAt.time else id
    return SignatureSongHistoryUiState(
        historyId = identifier,
        title = track.title,
        artist = track.artist,
        imageUrl = track.imageUrl,
        selectedAt = selectedAt,
        daysSinceSelected = DateUtils.getDaysSince(selectedAt, baseDate),
        isActive = isActive,
    )
}

fun List<SignatureSong>.toHistoryUiStates(): List<SignatureSongHistoryUiState> =
    mapIndexed { index, signatureSong ->
        val baseDate =
            if (index == 0) {
                Date()
            } else {
                val newerSong = this[index - 1]
                val normalizedNewerDate = DateUtils.normalizeDate(newerSong.selectedAt)
                Date(normalizedNewerDate.time - 1L)
            }
        signatureSong.toHistoryUiState(baseDate)
    }
