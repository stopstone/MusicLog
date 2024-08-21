package com.stopstone.myapplication.data.model

import android.content.Context
import com.stopstone.myapplication.R

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
    SATISFIED(R.string.emotion_satisfied);

    fun getDisplayName(context: Context): String {
        return context.getString(stringResId)
    }

    companion object {
        fun fromDisplayName(context: Context, displayName: String): Emotions? {
            return values().find { it.getDisplayName(context) == displayName }
        }
    }
}