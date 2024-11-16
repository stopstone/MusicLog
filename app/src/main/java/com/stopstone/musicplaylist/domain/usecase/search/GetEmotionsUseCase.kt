package com.stopstone.musicplaylist.domain.usecase.search

import com.stopstone.musicplaylist.domain.model.Emotions
import com.stopstone.musicplaylist.domain.repository.search.SearchRepository
import javax.inject.Inject

class GetEmotionsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(): List<Emotions> {
        return repository.getEmotions()
    }
}