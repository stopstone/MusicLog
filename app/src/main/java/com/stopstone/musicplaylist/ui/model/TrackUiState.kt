package com.stopstone.musicplaylist.ui.model

import android.os.Parcelable
import com.stopstone.musicplaylist.base.BaseIdModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackUiState(
    override val id: String,
    val imageUrl: String,
    val title: String,
    val artist: String
) : Parcelable, BaseIdModel