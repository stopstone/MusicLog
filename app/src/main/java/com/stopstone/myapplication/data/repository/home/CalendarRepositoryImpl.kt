package com.stopstone.myapplication.data.repository.home

import com.stopstone.myapplication.domain.model.CalendarDay
import com.stopstone.myapplication.domain.repository.home.CalendarRepository
import java.util.Calendar
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(): CalendarRepository {

    override fun getCalendarDates(year: Int, month: Int): List<CalendarDay> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, START_DAY)

        val dates = mutableListOf<CalendarDay>()
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // 현재 월의 1일 이전의 빈 칸 추가
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val emptyDays = firstDayOfWeek - Calendar.SUNDAY
        repeat(emptyDays) {
            dates.add(CalendarDay(EMPTY_DAY, year, month))
        }

        // 현재 월 날짜 추가
        for (day in 1..daysInMonth) {
            dates.add(CalendarDay(day, year, month))
        }

        return dates
    }

    companion object {
        private const val START_DAY = 1
        private const val EMPTY_DAY = 0
    }
}