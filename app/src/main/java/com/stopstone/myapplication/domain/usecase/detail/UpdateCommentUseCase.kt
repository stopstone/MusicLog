package com.stopstone.myapplication.domain.usecase.detail

import com.stopstone.myapplication.domain.repository.common.TrackRepository
import java.util.Date
import javax.inject.Inject

class UpdateCommentUseCase @Inject constructor(private val repository: TrackRepository) {
    suspend operator fun invoke(date: Date, comment: String) {
        repository.updateComment(date, comment)
    }
}