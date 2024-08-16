package com.stopstone.myapplication.domain.model

import android.os.Parcelable
import com.stopstone.myapplication.base.BaseIdModel
import com.stopstone.myapplication.data.model.Emotions
import com.stopstone.myapplication.ui.model.TrackUiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalendarDay(
    override val id: Int,
    val year: Int,
    val month: Int,
    val track: TrackUiState? = null,
    val emotions: List<Emotions> = emptyList(),
): Parcelable, BaseIdModel