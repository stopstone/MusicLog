package com.stopstone.myapplication.domain.usecase

import com.stopstone.myapplication.domain.model.TrackUiState
import com.stopstone.myapplication.domain.repository.PlayListRepository
import javax.inject.Inject

class GetPlayAllListUseCase @Inject constructor(
    private val repository: PlayListRepository
) {
    suspend operator fun invoke(): List<TrackUiState> {
        return repository.getAllPlayList()
    }

}