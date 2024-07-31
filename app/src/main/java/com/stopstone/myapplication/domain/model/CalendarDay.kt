package com.stopstone.myapplication.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalendarDay(
    val day: Int,
    val track: TrackUiState? = null
): Parcelable