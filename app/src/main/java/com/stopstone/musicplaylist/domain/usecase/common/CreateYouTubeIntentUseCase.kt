package com.stopstone.musicplaylist.domain.usecase.common

import android.content.Intent
import android.net.Uri
import javax.inject.Inject

class CreateYouTubeIntentUseCase @Inject constructor() {
    operator fun invoke(query: String): Intent {
        val encodedQuery = Uri.encode(query)
        val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://results?search_query=$encodedQuery"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$encodedQuery"))

        val chooser = Intent.createChooser(webIntent, "Open with")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(youtubeIntent))
        return chooser
    }
}