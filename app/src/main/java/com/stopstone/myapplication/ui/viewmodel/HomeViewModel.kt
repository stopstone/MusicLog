package com.stopstone.myapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stopstone.myapplication.data.model.CalendarDay
import com.stopstone.myapplication.data.repository.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
) : ViewModel() {
    private val _calendarDates = MutableLiveData<List<CalendarDay>>()
    val calendarDates: LiveData<List<CalendarDay>> = _calendarDates

    private val _currentMonth = MutableLiveData<String>()
    val currentMonth: LiveData<String> = _currentMonth

    private var currentYear: Int = EMPTY_YEAR
    private var currentMonthValue: Int = EMPTY_MONTH

    fun loadCalendar(year: Int, month: Int) {
        currentYear = year
        currentMonthValue = month

        _calendarDates.value = calendarRepository.getCalendarDates(year, month)
        _currentMonth.value = calendarRepository.getFormattedMonth(year, month)
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