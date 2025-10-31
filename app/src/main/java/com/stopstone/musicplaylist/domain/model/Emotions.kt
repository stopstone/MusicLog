package com.stopstone.musicplaylist.domain.model

import android.content.Context
import com.stopstone.musicplaylist.R

enum class Emotions(
    private val stringResId: Int,
) {
    HAPPY(R.string.emotion_happy),
    LOVING(R.string.emotion_loving),
    SAD(R.string.emotion_sad),
    ANGRY(R.string.emotion_angry),
    JOYFUL(R.string.emotion_joyful),
    ANXIOUS(R.string.emotion_anxious),
    CALM(R.string.emotion_calm),
    EXCITED(R.string.emotion_excited),
    BORED(R.string.emotion_bored),
    FEARFUL(R.string.emotion_fearful),
    GRATEFUL(R.string.emotion_grateful),
    HOPEFUL(R.string.emotion_hopeful),
    LONELY(R.string.emotion_lonely),
    MELANCHOLIC(R.string.emotion_melancholic),
    NOSTALGIC(R.string.emotion_nostalgic),
    PEACEFUL(R.string.emotion_peaceful),
    EMPOWERED(R.string.emotion_empowered),
    TOUCHED(R.string.emotion_touched),
    SENTIMENTAL(R.string.emotion_sentimental),
    FREE(R.string.emotion_free),
    COMFORTED(R.string.emotion_comforted),
    PASSIONATE(R.string.emotion_passionate),
    ;

    fun getDisplayName(context: Context): String = context.getString(stringResId)
}
