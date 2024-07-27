package com.stopstone.myapplication.domain.usecase

import com.stopstone.myapplication.domain.model.CalendarDay
import com.stopstone.myapplication.domain.repository.CalendarRepository
import javax.inject.Inject

class GetCalendarDatesUseCase @Inject constructor(
    private val repository: CalendarRepository
) {
    operator fun invoke(year: Int, month: Int): List<CalendarDay> {
        return repository.getCalendarDates(year, month)
    }
}