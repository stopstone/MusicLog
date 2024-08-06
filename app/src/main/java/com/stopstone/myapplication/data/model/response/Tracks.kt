package com.stopstone.myapplication.data.model.response

import android.os.Parcelable
import com.stopstone.myapplication.base.BaseIdModel
import kotlinx.parcelize.Parcelize

data class Tracks(
    val items: List<Track>,
)

@Parcelize
data class Track(
    override val id: String,
    val name: String,
    val artists: List<Artist>,
    val album: Album,
) : Parcelable, BaseIdModel

@Parcelize
data class Artist(
    val name: String,
) : Parcelable

@Parcelize
data class Album(
    val name: String,
    val images: List<Image>,
) : Parcelable

@Parcelize
data class Image(
    val url: String,
) : Parcelable