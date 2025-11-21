package com.stopstone.musicplaylist.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    // 상수 정의
    private const val MONTHS_IN_YEAR = 12
    private const val FIRST_MONTH = 1
    private const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000L

    // 오늘 날짜를 반환 (시간 정보 제거)
    fun getTodayDate(): Date = Calendar.getInstance().clearTime().time

    // 현재 년도를 반환
    fun getCurrentYear(): Int = Calendar.getInstance().getYear()

    // 현재 월을 반환 (1-12)
    fun getCurrentMonth(): Int = Calendar.getInstance().getMonth()

    // 년월을 현재 시스템 Locale 형식으로 포맷팅
    fun getFormattedMonth(
        year: Int,
        month: Int,
    ): String {
        val date = createDate(year, month, 1)

        // 시스템 Locale에 따라 적절한 포맷 사용
        val locale = Locale.getDefault()
        val dateFormat =
            when {
                locale.language == "ko" -> SimpleDateFormat("yyyy년 MM월", locale)
                else -> SimpleDateFormat("MM yyyy", locale)
            }

        return dateFormat.format(date)
    }

    // 다음 월 계산
    fun getNextMonth(
        year: Int,
        month: Int,
    ): Pair<Int, Int> {
        var nextMonth = month + 1
        var nextYear = year

        if (nextMonth > MONTHS_IN_YEAR) {
            nextMonth = FIRST_MONTH
            nextYear++
        }

        return Pair(nextYear, nextMonth)
    }

    // 이전 월 계산
    fun getPreviousMonth(
        year: Int,
        month: Int,
    ): Pair<Int, Int> {
        var previousMonth = month - 1
        var previousYear = year

        if (previousMonth < FIRST_MONTH) {
            previousMonth = MONTHS_IN_YEAR
            previousYear--
        }

        return Pair(previousYear, previousMonth)
    }

    // 날짜 정규화 (시간 정보를 00:00:00으로 설정)
    fun normalizeDate(date: Date): Date =
        Calendar
            .getInstance()
            .apply {
                time = date
                clearTime()
            }.time

    fun getStartOfDay(date: Date): Date =
        Calendar
            .getInstance()
            .apply {
                time = date
                clearTime()
            }.time

    fun getEndOfDay(date: Date): Date =
        Calendar
            .getInstance()
            .apply {
                time = date
                setEndOfDay()
            }.time

    // 특정 날짜의 일(day)을 반환
    fun getDayFromDate(date: Date): Int = Calendar.getInstance().apply { time = date }.getDay()

    // 년, 월, 일로 Date 객체 생성 (시간은 00:00:00으로 설정)
    fun createDate(
        year: Int,
        month: Int,
        day: Int,
    ): Date =
        Calendar
            .getInstance()
            .apply {
                set(year, month - 1, day, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

    // Calendar 확장 함수들
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

    fun Calendar.getMonthStart(
        year: Int,
        month: Int,
    ): Date {
        apply {
            set(year, month - 1, 1)
            clearTime()
        }
        return time
    }

    fun Calendar.getMonthEnd(
        year: Int,
        month: Int,
    ): Date {
        apply {
            set(year, month - 1, getActualMaximum(Calendar.DAY_OF_MONTH))
            setEndOfDay()
        }
        return time
    }

    fun Calendar.getYear(): Int = get(Calendar.YEAR)

    fun Calendar.getMonth(): Int = get(Calendar.MONTH) + 1

    fun Calendar.getDay(): Int = get(Calendar.DAY_OF_MONTH)

    // 현재 시간을 밀리초로 반환
    fun getCurrentTimeMillis(): Long = System.currentTimeMillis()

    // 인생곡 선택 이후 지난 일수를 계산
    fun getDaysSince(
        date: Date,
        baseDate: Date = Date(),
    ): Int {
        val timeDiff = baseDate.time - date.time
        if (timeDiff <= 0L) {
            return 1
        }
        val days = (timeDiff / MILLIS_IN_DAY).toInt()
        return days.coerceAtLeast(0) + 1
    }

    fun formatWithPattern(
        date: Date,
        pattern: String,
    ): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }

    // 캘린더 그리드에 필요한 빈 칸 수 계산
    fun getEmptyDaysCount(
        year: Int,
        month: Int,
    ): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return firstDayOfWeek - Calendar.SUNDAY
    }

    // 월의 총 일수 반환
    fun getDaysInMonth(
        year: Int,
        month: Int,
    ): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // Date에서 시각(0-23) 추출
    fun getHourOfDay(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    // Date에서 분(0-59) 추출
    fun getMinute(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.MINUTE)
    }

    // Date를 하루의 총 분(0-1440)으로 변환
    fun getTotalMinutes(date: Date): Int {
        val hour = getHourOfDay(date)
        val minute = getMinute(date)
        return hour * 60 + minute
    }

    // Date를 "HH:mm" 형식으로 포맷팅
    fun formatTime(date: Date): String {
        val hour = getHourOfDay(date)
        val minute = getMinute(date)
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }
}
