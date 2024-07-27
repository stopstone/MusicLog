package com.stopstone.myapplication.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackUiState(
    val id: String,
    val imageUrl: String?,
    val title: String,
    val artist: String
) : Parcelable