package com.stopstone.musicplaylist.domain.usecase.common

import com.stopstone.musicplaylist.ui.model.TrackUiState
import com.stopstone.musicplaylist.domain.repository.common.PlayListRepository
import javax.inject.Inject

class GetPlayAllListUseCase @Inject constructor(
    private val repository: PlayListRepository
) {
    suspend operator fun invoke(): List<TrackUiState> {
        return repository.getAllPlayList()
    }

}