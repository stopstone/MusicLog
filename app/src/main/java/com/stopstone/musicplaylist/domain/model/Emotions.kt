package com.stopstone.musicplaylist.domain.model

import android.content.Context
import com.stopstone.musicplaylist.R

enum class Emotions(val stringResId: Int) {
    HAPPY(R.string.emotion_happy),
    SAD(R.string.emotion_sad),
    ANGRY(R.string.emotion_angry),
    JOYFUL(R.string.emotion_joyful),
    ANXIOUS(R.string.emotion_anxious),
    CALM(R.string.emotion_calm),
    EXCITED(R.string.emotion_excited),
    BORED(R.string.emotion_bored),
    CONFIDENT(R.string.emotion_confident),
    FEARFUL(R.string.emotion_fearful),
    GRATEFUL(R.string.emotion_grateful),
    FRUSTRATED(R.string.emotion_frustrated),
    HOPEFUL(R.string.emotion_hopeful),
    LONELY(R.string.emotion_lonely),
    LOVING(R.string.emotion_loving),
    JEALOUS(R.string.emotion_jealous),
    REGRETFUL(R.string.emotion_regretful),
    SURPRISED(R.string.emotion_surprised),
    CONFUSED(R.string.emotion_confused),
    SATISFIED(R.string.emotion_satisfied),
    MELANCHOLIC(R.string.emotion_melancholic),
    NOSTALGIC(R.string.emotion_nostalgic),
    DREAMY(R.string.emotion_dreamy),
    ENERGETIC(R.string.emotion_energetic),
    PEACEFUL(R.string.emotion_peaceful),
    ROMANTIC(R.string.emotion_romantic),
    EMPOWERED(R.string.emotion_empowered),
    REFLECTIVE(R.string.emotion_reflective),
    TOUCHED(R.string.emotion_touched),
    UPLIFTED(R.string.emotion_uplifted),
    DETERMINED(R.string.emotion_determined),
    SENTIMENTAL(R.string.emotion_sentimental),
    FREE(R.string.emotion_free),
    COMFORTED(R.string.emotion_comforted),
    PASSIONATE(R.string.emotion_passionate);

    fun getDisplayName(context: Context): String {
        return context.getString(stringResId)
    }
}