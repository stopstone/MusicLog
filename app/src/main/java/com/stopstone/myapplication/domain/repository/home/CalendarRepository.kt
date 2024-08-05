package com.stopstone.myapplication.domain.repository.home

import com.stopstone.myapplication.domain.model.CalendarDay


interface CalendarRepository {
    fun getCalendarDates(year: Int, month: Int): List<CalendarDay>
}