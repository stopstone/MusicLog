package com.stopstone.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.data.model.Track
import com.stopstone.myapplication.domain.model.TrackUiState
import com.stopstone.myapplication.domain.usecase.SearchTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchTracksUseCase: SearchTracksUseCase
) : ViewModel() {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchState: StateFlow<SearchState> = _searchState

    fun searchTracks(query: String) = viewModelScope.launch {
        _searchState.value = SearchState.Loading
        searchTracksUseCase(query).fold(
            onSuccess = { tracks ->
                _searchState.value = SearchState.Success(
                    tracks.map { it.toUiState() }
                )
            },
            onFailure = { exception ->
                _searchState.value = SearchState.Error(exception.message ?: "Unknown error")
            }
        )
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

sealed class SearchState {
    object Initial : SearchState()
    object Loading : SearchState()
    data class Success(val tracks: List<TrackUiState>) : SearchState()
    data class Error(val message: String) : SearchState()
}