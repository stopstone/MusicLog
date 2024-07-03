package com.stopstone.myapplication.data.repository

import com.stopstone.myapplication.data.model.CalendarDay
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class CalendarRepository @Inject constructor() {

    fun getCalendarDates(year: Int, month: Int): List<CalendarDay> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, START_DAY)

        val dates = mutableListOf<CalendarDay>()
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // 현재 월의 1일 이전의 빈 칸 추가
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val emptyDays = firstDayOfWeek - Calendar.SUNDAY
        repeat(emptyDays) {
            dates.add(CalendarDay(EMPTY_DAY))
        }

        // 현재 월 날짜 추가
        for (day in 1..daysInMonth) {
            dates.add(CalendarDay(day))
        }

        return dates
    }

    fun getFormattedMonth(year: Int, month: Int): String {
        return String.format(Locale.getDefault(), MONTH_FORMAT, year, month)
    }

    companion object {
        private const val MONTH_FORMAT = "%d년 %d월"
        private const val START_DAY = 1
        private const val EMPTY_DAY = 0
    }
}