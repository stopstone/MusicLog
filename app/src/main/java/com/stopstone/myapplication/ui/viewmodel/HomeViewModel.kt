package com.stopstone.myapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.domain.model.CalendarDay
import com.stopstone.myapplication.data.model.DailyTrack
import com.stopstone.myapplication.domain.usecase.GetCalendarDatesUseCase
import com.stopstone.myapplication.domain.usecase.GetFormattedMonthUseCase
import com.stopstone.myapplication.domain.usecase.GetTodayTrackUseCase
import com.stopstone.myapplication.domain.usecase.GetTracksForMonthUseCase
import com.stopstone.myapplication.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCalendarDatesUseCase: GetCalendarDatesUseCase,
    private val getFormattedMonthUseCase: GetFormattedMonthUseCase,
    private val getTodayTrackUseCase: GetTodayTrackUseCase,
    private val getTracksForMonthUseCase: GetTracksForMonthUseCase
) : ViewModel() {
    private val _calendarDates = MutableStateFlow<List<CalendarDay>>(emptyList())
    val calendarDates: StateFlow<List<CalendarDay>> = _calendarDates.asStateFlow()

    private val _currentMonth = MutableStateFlow("")
    val currentMonth: StateFlow<String> = _currentMonth.asStateFlow()

    private val _todayTrack = MutableStateFlow<DailyTrack?>(null)
    val todayTrack: StateFlow<DailyTrack?> = _todayTrack

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
        _currentMonth.value = getFormattedMonthUseCase(year, month)

        val calendar = Calendar.getInstance()
        val updatedCalendarDays = calendarDays.map { calendarDay ->
            val track = tracksForMonth.find { dailyTrack ->
                calendar.time = dailyTrack.date
                calendar.get(Calendar.DAY_OF_MONTH) == calendarDay.day
            }?.track
            calendarDay.copy(track = track)
        }

        _calendarDates.value = updatedCalendarDays
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
    }
}