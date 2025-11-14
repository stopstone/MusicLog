package com.stopstone.musicplaylist.ui.common

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.util.DateUtils
import java.util.Date

object SignatureSongTextFormatter {
    fun buildSinceText(
        context: Context,
        selectedDate: Date,
        daysSinceSelected: Int,
    ): CharSequence {
        val pattern = context.getString(R.string.format_signature_song_date)
        val selectedDateLabel = DateUtils.formatWithPattern(selectedDate, pattern)
        val daySuffix = context.getString(R.string.label_signature_song_day_suffix)
        val template =
            context.getString(
                R.string.label_signature_song_since_detail,
                selectedDateLabel,
                daysSinceSelected,
                daySuffix,
            )
        val spannable = SpannableString(template)
        val target = "$daysSinceSelected$daySuffix"
        val start = template.lastIndexOf(target)
        if (start >= 0) {
            val end = start + target.length
            val boldSpan = StyleSpan(Typeface.BOLD)
            val colorSpan = ForegroundColorSpan(ContextCompat.getColor(context, R.color.black))
            spannable.setSpan(boldSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannable
    }

    fun buildDaysOnlyText(
        context: Context,
        daysSinceSelected: Int,
    ): CharSequence {
        val daySuffix = context.getString(R.string.label_signature_song_day_suffix)
        val template =
            context.getString(
                R.string.label_signature_song_since_days_only,
                daysSinceSelected,
                daySuffix,
            )
        val spannable = SpannableString(template)
        val target = "$daysSinceSelected$daySuffix"
        val start = template.indexOf(target)
        if (start >= 0) {
            val end = start + target.length
            val boldSpan = StyleSpan(Typeface.BOLD)
            val colorSpan = ForegroundColorSpan(ContextCompat.getColor(context, R.color.black))
            spannable.setSpan(boldSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannable
    }
}

