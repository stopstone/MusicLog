package com.stopstone.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.data.model.Track
import com.stopstone.myapplication.domain.model.TrackUiState
import com.stopstone.myapplication.domain.usecase.SearchTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchTracksUseCase: SearchTracksUseCase,
) : ViewModel() {
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracksUiState: Flow<List<TrackUiState>> = _tracks.map { tracks ->
        tracks.map { it.toUiState() }
    }

    fun searchTracks(query: String) = viewModelScope.launch {
        val tracks = searchTracksUseCase(query)
        _tracks.value = tracks
    }


    private fun Track.toUiState(): TrackUiState {
        return TrackUiState(
            id = id,
            imageUrl = album.images.firstOrNull()?.url,
            title = name,
            artist = artists.joinToString(", ") { it.name }
        )
    }
}