package com.stopstone.musicplaylist.data.model.dto

import com.google.gson.annotations.SerializedName
import com.stopstone.musicplaylist.data.model.response.Album
import com.stopstone.musicplaylist.data.model.response.Artist
import com.stopstone.musicplaylist.data.model.response.Image
import com.stopstone.musicplaylist.data.model.response.Track

private const val ARTWORK_IMAGE_SMALL = "100x100bb"
private const val ARTWORK_IMAGE_LARGE = "600x600bb"
private val FEAT_REGEX =
    Regex("""[(\[]\s*(?:feat|ft)\.?\s*([^)\]]+)[)\]]""", RegexOption.IGNORE_CASE)
private val FEAT_SPLIT_REGEX = Regex("""\s*(?:,|&|feat\.?|ft\.?)\s*""", RegexOption.IGNORE_CASE)

data class ITunesSongDto(
    @SerializedName("trackId") val trackId: Long,
    @SerializedName("trackName") val trackName: String?,
    @SerializedName("artistName") val artistName: String?,
    @SerializedName("collectionName") val conllectionName: String?,
    @SerializedName("artworkUrl100") val artworkUrl100: String?,
)

fun ITunesSongDto.toTrack(): Track =
    Track(
        id = trackId.toString(),
        name = trackName.toString(),
        artists = buildArtists(),
        album =
            Album(
                name = conllectionName.toString(),
                images = buildImages(),
            ),
    )

private fun ITunesSongDto.buildArtists(): List<Artist> {
    val primaryName = artistName.orEmpty()
    val featuredNames = extractFeaturedNames(trackName.orEmpty())
    val allNames = (listOf(primaryName) + featuredNames).filter { it.isNotEmpty() }.distinct()
    return allNames.map { Artist(name = it) }
}

private fun ITunesSongDto.extractFeaturedNames(title: String): List<String> {
    val matched = FEAT_REGEX.find(title)?.groupValues?.get(1) ?: return emptyList()
    return matched
        .split(FEAT_SPLIT_REGEX)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}

private fun ITunesSongDto.buildImages(): List<Image> {
    val artwork = artworkUrl100 ?: return emptyList()
    return listOf(
        Image(
            url = artwork.replace(ARTWORK_IMAGE_SMALL, ARTWORK_IMAGE_LARGE),
        ),
    )
}
