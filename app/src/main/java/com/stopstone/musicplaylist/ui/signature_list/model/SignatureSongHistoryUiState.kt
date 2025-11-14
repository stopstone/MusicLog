package com.stopstone.musicplaylist.ui.signature_list.model

import com.stopstone.musicplaylist.base.BaseIdModel
import com.stopstone.musicplaylist.data.model.entity.SignatureSong
import com.stopstone.musicplaylist.util.DateUtils

data class SignatureSongHistoryUiState(
    val historyId: Long,
    val title: String,
    val artist: String,
    val imageUrl: String,
    val selectedAtLabel: String,
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

fun SignatureSong.toHistoryUiState(): SignatureSongHistoryUiState {
    val identifier = if (id == 0L) selectedAt.time else id
    return SignatureSongHistoryUiState(
        historyId = identifier,
        title = track.title,
        artist = track.artist,
        imageUrl = track.imageUrl,
        selectedAtLabel = DateUtils.formatSignatureSongDate(selectedAt),
        isActive = isActive,
    )
}
