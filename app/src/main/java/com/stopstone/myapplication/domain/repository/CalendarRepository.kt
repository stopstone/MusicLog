package com.stopstone.myapplication.domain.repository

import com.stopstone.myapplication.domain.model.CalendarDay


interface CalendarRepository {
    fun getCalendarDates(year: Int, month: Int): List<CalendarDay>
    fun getFormattedMonth(year: Int, month: Int): String
}