package com.stopstone.musicplaylist.domain.repository.home

import com.stopstone.musicplaylist.domain.model.CalendarDay


interface CalendarRepository {
    fun getCalendarDates(year: Int, month: Int): List<CalendarDay>
}