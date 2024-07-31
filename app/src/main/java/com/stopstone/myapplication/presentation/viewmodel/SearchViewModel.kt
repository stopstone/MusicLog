package com.stopstone.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.data.model.entity.SearchHistory
import com.stopstone.myapplication.data.model.response.Track
import com.stopstone.myapplication.domain.model.TrackUiState
import com.stopstone.myapplication.domain.usecase.AddSearchUseCase
import com.stopstone.myapplication.domain.usecase.DeleteAllSearchesUseCase
import com.stopstone.myapplication.domain.usecase.DeleteSearchUseCase
import com.stopstone.myapplication.domain.usecase.GetAllSearchHistoryUseCase
import com.stopstone.myapplication.domain.usecase.SearchTracksUseCase
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

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchState: StateFlow<SearchState> = _searchState

    private val _searchHistory = MutableStateFlow<List<SearchHistory>>(emptyList())
    val searchHistory: StateFlow<List<SearchHistory>> = _searchHistory.asStateFlow()

    fun loadSearchHistory() = viewModelScope.launch {
        getAllSearchHistoryUseCase().collect { history ->
            _searchHistory.value = history
        }
    }

    fun searchTracks(query: String) = viewModelScope.launch {
        searchTracksUseCase(query).fold(
            onSuccess = { tracks ->
                _searchState.value = SearchState.Success(
                    tracks.map { it.toTrackUiState() }
                )
            },
            onFailure = { exception ->
                _searchState.value = SearchState.Error(exception.message ?: "Unknown error")
            }
        )
    }

    fun addSearch(query: String) = viewModelScope.launch {
        addSearchUseCase(query)
    }

    fun deleteSearch(search: SearchHistory) = viewModelScope.launch {
        deleteSearchUseCase(search)
    }

    fun clearAllSearches() = viewModelScope.launch {
        deleteAllSearchesUseCase()
    }

    private fun Track.toTrackUiState(): TrackUiState =
        TrackUiState(
            id = id,
            imageUrl = album.images.first().url,
            title = name,
            artist = artists.joinToString(", ") { it.name }
        )
}
sealed class SearchState {
    object Initial : SearchState()
    data class Success(val tracks: List<TrackUiState>) : SearchState()
    data class Error(val message: String) : SearchState()
}