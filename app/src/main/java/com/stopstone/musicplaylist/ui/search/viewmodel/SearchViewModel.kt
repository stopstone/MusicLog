package com.stopstone.musicplaylist.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.data.model.entity.SearchHistory
import com.stopstone.musicplaylist.data.model.response.Track
import com.stopstone.musicplaylist.domain.usecase.search.AddSearchUseCase
import com.stopstone.musicplaylist.domain.usecase.search.DeleteAllSearchesUseCase
import com.stopstone.musicplaylist.domain.usecase.search.DeleteSearchUseCase
import com.stopstone.musicplaylist.domain.usecase.search.GetAllSearchHistoryUseCase
import com.stopstone.musicplaylist.domain.usecase.search.SearchTracksUseCase
import com.stopstone.musicplaylist.ui.model.TrackUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val getAllSearchHistoryUseCase: GetAllSearchHistoryUseCase,
    private val addSearchUseCase: AddSearchUseCase,
    private val deleteSearchUseCase: DeleteSearchUseCase,
    private val deleteAllSearchesUseCase: DeleteAllSearchesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<SearchHistory>>(emptyList())
    val searchHistory: StateFlow<List<SearchHistory>> = _searchHistory.asStateFlow()

    init {
        loadSearchHistory()
    }

    private fun loadSearchHistory() = viewModelScope.launch {
        getAllSearchHistoryUseCase().collect { history ->
            _searchHistory.value = history
            if (_uiState.value is SearchUiState.Initial) {
                _uiState.value = SearchUiState.ShowHistory
            }
        }
    }

    fun searchTracks(query: String) = viewModelScope.launch {
        _uiState.value = SearchUiState.Loading

        runCatching { searchTracksUseCase(query) }
            .onSuccess { tracks ->
                val trackUiStates = tracks.map { it.toTrackUiState() }
                _uiState.value = if (trackUiStates.isEmpty()) {
                    SearchUiState.Empty
                } else {
                    SearchUiState.Success(trackUiStates)
                }
                addSearch(query)
            }
            .onFailure {
                _uiState.value = SearchUiState.Error
            }
    }

    private fun addSearch(query: String) = viewModelScope.launch {
        addSearchUseCase(query)
    }

    fun deleteSearch(search: SearchHistory) = viewModelScope.launch {
        deleteSearchUseCase(search)
    }

    fun clearAllSearches() = viewModelScope.launch {
        deleteAllSearchesUseCase()
    }

    fun resetToHistory() {
        _uiState.value = SearchUiState.ShowHistory
    }

    private fun Track.toTrackUiState(): TrackUiState =
        TrackUiState(
            id = id,
            imageUrl = album.images.first().url,
            title = name,
            artist = artists.joinToString(", ") { it.name },
        )
}

sealed class SearchUiState {
    object Initial : SearchUiState()
    object ShowHistory : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val tracks: List<TrackUiState>) : SearchUiState()
    object Empty : SearchUiState()
    object Error : SearchUiState()
}