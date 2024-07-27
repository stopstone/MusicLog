package com.stopstone.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.data.model.Track
import com.stopstone.myapplication.domain.model.TrackUiState
import com.stopstone.myapplication.domain.usecase.GetPlayAllListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayListViewModel @Inject constructor(
    private val getAllPlayListUseCase: GetPlayAllListUseCase,
) : ViewModel() {
    private val _tracks = MutableStateFlow<List<TrackUiState>>(emptyList())
    val tracks: StateFlow<List<TrackUiState>> = _tracks.asStateFlow()

    fun getAllPlayList() = viewModelScope.launch {
        val tracks = getAllPlayListUseCase()
        _tracks.value = tracks
    }
}
