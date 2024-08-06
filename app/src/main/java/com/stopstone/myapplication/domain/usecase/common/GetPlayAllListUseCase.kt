package com.stopstone.myapplication.domain.usecase.common

import com.stopstone.myapplication.ui.model.TrackUiState
import com.stopstone.myapplication.domain.repository.common.PlayListRepository
import javax.inject.Inject

class GetPlayAllListUseCase @Inject constructor(
    private val repository: PlayListRepository
) {
    suspend operator fun invoke(): List<TrackUiState> {
        return repository.getAllPlayList()
    }

}