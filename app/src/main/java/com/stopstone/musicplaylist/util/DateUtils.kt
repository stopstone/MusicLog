package com.stopstone.musicplaylist.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    // 상수 정의
    private const val MONTHS_IN_YEAR = 12
    private const val FIRST_MONTH = 1

    // 오늘 날짜를 반환 (시간 정보 제거)
    fun getTodayDate(): Date {
        return Calendar.getInstance().clearTime().time
    }

    // 현재 년도를 반환
    fun getCurrentYear(): Int {
        return Calendar.getInstance().getYear()
    }

    // 현재 월을 반환 (1-12)
    fun getCurrentMonth(): Int {
        return Calendar.getInstance().getMonth()
    }

    // 년월을 현재 시스템 Locale 형식으로 포맷팅
    fun getFormattedMonth(year: Int, month: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1) // month는 0부터 시작하므로 -1
        
        // 시스템 Locale에 따라 적절한 포맷 사용
        val locale = Locale.getDefault()
        val dateFormat = when {
            locale.language == "ko" -> SimpleDateFormat("yyyy년 M월", locale)
            else -> SimpleDateFormat("MM yyyy", locale)
        }
        
        return dateFormat.format(calendar.time)
    }

    // 다음 월 계산
    fun getNextMonth(year: Int, month: Int): Pair<Int, Int> {
        var nextMonth = month + 1
        var nextYear = year

        if (nextMonth > MONTHS_IN_YEAR) {
            nextMonth = FIRST_MONTH
            nextYear++
        }

        return Pair(nextYear, nextMonth)
    }

    // 이전 월 계산
    fun getPreviousMonth(year: Int, month: Int): Pair<Int, Int> {
        var previousMonth = month - 1
        var previousYear = year

        if (previousMonth < FIRST_MONTH) {
            previousMonth = MONTHS_IN_YEAR
            previousYear--
        }

        return Pair(previousYear, previousMonth)
    }

    // 날짜가 오늘인지 확인
    fun isToday(year: Int, month: Int, day: Int): Boolean {
        val today = Calendar.getInstance()
        return year == today.getYear() &&
                month == today.getMonth() &&
                day == today.getDay()
    }

    // 날짜 정규화 (시간 정보를 00:00:00으로 설정)
    fun normalizeDate(date: Date): Date {
        return Calendar.getInstance().apply {
            time = date
            clearTime()
        }.time
    }

    // 특정 날짜의 일(day)을 반환
    fun getDayFromDate(date: Date): Int {
        return Calendar.getInstance().apply { time = date }.getDay()
    }

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