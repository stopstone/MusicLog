package com.stopstone.musicplaylist.domain.usecase.home

import com.stopstone.musicplaylist.domain.model.CalendarDay
import com.stopstone.musicplaylist.domain.repository.home.CalendarRepository
import javax.inject.Inject

class GetCalendarDatesUseCase @Inject constructor(
    private val repository: CalendarRepository
) {
    operator fun invoke(year: Int, month: Int): List<CalendarDay> {
        return repository.getCalendarDates(year, month)
    }
}