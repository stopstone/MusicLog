package com.stopstone.myapplication.domain.usecase

import com.stopstone.myapplication.domain.repository.CalendarRepository
import javax.inject.Inject

class GetFormattedMonthUseCase @Inject constructor(
    private val repository: CalendarRepository
) {
    operator fun invoke(year: Int, month: Int): String {
        return repository.getFormattedMonth(year, month)
    }
}