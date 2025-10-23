package com.stopstone.musicplaylist.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.data.model.entity.DailyTrack
import com.stopstone.musicplaylist.data.model.response.Track
import com.stopstone.musicplaylist.domain.model.CalendarDay
import com.stopstone.musicplaylist.domain.usecase.home.GetCalendarDatesUseCase
import com.stopstone.musicplaylist.domain.usecase.home.GetTodayTrackUseCase
import com.stopstone.musicplaylist.domain.usecase.home.GetTracksForMonthUseCase
import com.stopstone.musicplaylist.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCalendarDatesUseCase: GetCalendarDatesUseCase,
    private val getTodayTrackUseCase: GetTodayTrackUseCase,
    private val getTracksForMonthUseCase: GetTracksForMonthUseCase,
) : ViewModel() {
    private val _calendarDates = MutableStateFlow<List<CalendarDay>>(emptyList())
    val calendarDates: StateFlow<List<CalendarDay>> = _calendarDates.asStateFlow()

    private val _currentMonth = MutableStateFlow("")
    val currentMonth: StateFlow<String> = _currentMonth.asStateFlow()

    private val _todayTrack = MutableStateFlow<DailyTrack?>(null)
    val todayTrack: StateFlow<DailyTrack?> = _todayTrack

    private val _recommendations = MutableStateFlow<List<Track>>(emptyList())
    val recommendations: StateFlow<List<Track>> = _recommendations.asStateFlow()


    private var currentYear: Int = EMPTY_YEAR
    private var currentMonthValue: Int = EMPTY_MONTH

    fun loadTodayTrack() = viewModelScope.launch {
        val today = DateUtils.getTodayDate()
        val track = getTodayTrackUseCase(today)
        _todayTrack.value = track
    }

    fun loadCalendar(year: Int, month: Int) = viewModelScope.launch {
        currentYear = year
        currentMonthValue = month

        val calendarDays = getCalendarDatesUseCase(year, month)
        val tracksForMonth = getTracksForMonthUseCase(year, month)
        _currentMonth.value = getFormattedMonth(year, month)

        val calendar = Calendar.getInstance()
        val updatedCalendarDays = calendarDays.map { calendarDay ->
            val dailyTrack = tracksForMonth.find { dailyTrack ->
                calendar.time = dailyTrack.date
                calendar.get(Calendar.DAY_OF_MONTH) == calendarDay.id
            }
            calendarDay.copy(
                track = dailyTrack?.track,
                emotions = dailyTrack?.emotions ?: emptyList()
            )
        }
        _calendarDates.value = updatedCalendarDays
    }

    private fun getFormattedMonth(year: Int, month: Int): String {
        return String.format(Locale.KOREA, MONTH_FORMAT, year, month)
    }

    fun nextMonth() {
        currentMonthValue++
        if (currentMonthValue > MONTHS_IN_YEAR) {
            currentMonthValue = FIRST_MONTH
            currentYear++
        }
        loadCalendar(currentYear, currentMonthValue)
    }

    fun previousMonth() {
        currentMonthValue--
        if (currentMonthValue < FIRST_MONTH) {
            currentMonthValue = MONTHS_IN_YEAR
            currentYear--
        }
        loadCalendar(currentYear, currentMonthValue)
    }

    companion object {
        private const val MONTHS_IN_YEAR = 12
        private const val FIRST_MONTH = 1
        private const val EMPTY_YEAR = 0
        private const val EMPTY_MONTH = 0
        private const val MONTH_FORMAT = "%d년 %d월"
    }
}