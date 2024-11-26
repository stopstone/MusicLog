package com.stopstone.musicplaylist.domain.model

import android.os.Parcelable
import com.stopstone.musicplaylist.base.BaseIdModel
import com.stopstone.musicplaylist.ui.model.TrackUiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalendarDay(
    override val id: Int,
    val year: Int,
    val month: Int,
    val track: TrackUiState? = null,
    val emotions: List<Emotions> = emptyList(),
    val isToday: Boolean = false,
): Parcelable, BaseIdModel