package com.stopstone.musicplaylist.data.model.response

import com.google.gson.annotations.SerializedName
import com.stopstone.musicplaylist.data.model.dto.ITunesSongDto

data class ITunesSearchResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<ITunesSongDto>,
)
