package com.stopstone.musicplaylist.data.repository.home

import com.stopstone.musicplaylist.domain.model.CalendarDay
import com.stopstone.musicplaylist.domain.repository.home.CalendarRepository
import com.stopstone.musicplaylist.util.DateUtils.getDay
import com.stopstone.musicplaylist.util.DateUtils.getMonth
import com.stopstone.musicplaylist.util.DateUtils.getYear
import java.util.Calendar
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(): CalendarRepository {

    override fun getCalendarDates(year: Int, month: Int): List<CalendarDay> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, START_DAY) // 월은 0부터 시작하므로 -1

        val dates = mutableListOf<CalendarDay>()
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // 오늘 날짜 계산
        val today = Calendar.getInstance()
        val todayYear = today.getYear()
        val todayMonth = today.getMonth()
        val todayDay = today.getDay()

        // 현재 월의 1일 이전의 빈 칸 추가
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val emptyDays = firstDayOfWeek - Calendar.SUNDAY
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
        for (day in 1..daysInMonth) {
            dates.add(
                CalendarDay(
                    id = day,
                    year = year,
                    month = month,
                    isToday = (year == todayYear && month == todayMonth && day == todayDay) // 오늘인지 비교
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