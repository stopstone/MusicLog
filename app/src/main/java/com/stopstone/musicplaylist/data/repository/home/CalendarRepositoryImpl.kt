package com.stopstone.musicplaylist.data.repository.home

import com.stopstone.musicplaylist.domain.model.CalendarDay
import com.stopstone.musicplaylist.domain.repository.home.CalendarRepository
import com.stopstone.musicplaylist.util.DateUtils
import com.stopstone.musicplaylist.util.DateUtils.getDay
import com.stopstone.musicplaylist.util.DateUtils.getMonth
import com.stopstone.musicplaylist.util.DateUtils.getYear
import java.util.Calendar
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(): CalendarRepository {

    override fun getCalendarDates(year: Int, month: Int): List<CalendarDay> {
        val dates = mutableListOf<CalendarDay>()
        val daysInMonth = DateUtils.getDaysInMonth(year, month)
        val emptyDays = DateUtils.getEmptyDaysCount(year, month)

        // 현재 월의 1일 이전의 빈 칸 추가
        repeat(emptyDays) {
            dates.add(
                CalendarDay(
                    id = EMPTY_DAY,
                    year = year,
                    month = month,
                )
            )
        }

        // 현재 월 날짜 추가
        val today = DateUtils.getTodayDate()
        val todayCalendar = Calendar.getInstance().apply { time = today }
        
        for (day in 1..daysInMonth) {
            val isToday = year == todayCalendar.getYear() &&
                    month == todayCalendar.getMonth() &&
                    day == todayCalendar.getDay()
            
            dates.add(
                CalendarDay(
                    id = day,
                    year = year,
                    month = month,
                    isToday = isToday,
                )
            )
        }

        return dates
    }

    companion object {
        private const val START_DAY = 1
        private const val EMPTY_DAY = 0
    }
}