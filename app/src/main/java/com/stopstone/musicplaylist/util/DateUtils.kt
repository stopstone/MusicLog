package com.stopstone.musicplaylist.util

import java.util.Calendar
import java.util.Date


object DateUtils {
    fun getTodayDate(): Date {
        return Calendar.getInstance().clearTime().time
    }

    private fun Calendar.clearTime(): Calendar {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        return this
    }

    private fun Calendar.setEndOfDay(): Calendar {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
        return this
    }

    fun Calendar.getMonthStart(year: Int, month: Int): Date {
        apply {
            set(year, month - 1, 1)
            clearTime()
        }
        return time
    }

    fun Calendar.getMonthEnd(year: Int, month: Int): Date {
        apply {
            set(year, month - 1, getActualMaximum(Calendar.DAY_OF_MONTH))
            setEndOfDay()
        }
        return time
    }

    fun Calendar.getYear(): Int = get(Calendar.YEAR)

    fun Calendar.getMonth(): Int = get(Calendar.MONTH) + 1

    fun Calendar.getDay(): Int = get(Calendar.DAY_OF_MONTH)
}