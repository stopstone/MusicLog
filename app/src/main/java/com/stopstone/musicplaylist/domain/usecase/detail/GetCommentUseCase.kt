package com.stopstone.musicplaylist.domain.usecase.detail

import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
import java.util.Date
import javax.inject.Inject

class GetCommentUseCase @Inject constructor(private val repository: TrackRepository) {
    suspend operator fun invoke(date: Date): String {
        return repository.getComment(date) ?: ""
    }
}