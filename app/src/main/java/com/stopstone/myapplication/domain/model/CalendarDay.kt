package com.stopstone.myapplication.domain.model

import android.os.Parcelable
import com.stopstone.myapplication.base.BaseIdModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalendarDay(
    override val id: Int,
    val track: TrackUiState? = null
): Parcelable, BaseIdModel