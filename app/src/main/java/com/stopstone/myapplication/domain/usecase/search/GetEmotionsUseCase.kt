package com.stopstone.myapplication.domain.usecase.search

import com.stopstone.myapplication.data.model.Emotions
import com.stopstone.myapplication.domain.repository.search.SearchRepository
import javax.inject.Inject

class GetEmotionsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(): List<Emotions> {
        return repository.getEmotions()
    }
}