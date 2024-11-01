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
    private val _searchHistory = MutableStateFlow<List<SearchHistory>>(emptyList())
    val searchHistory: StateFlow<List<SearchHistory>> = _searchHistory.asStateFlow()

    private val _searchList = MutableStateFlow<List<TrackUiState>>(emptyList())
    val searchList: StateFlow<List<TrackUiState>> = _searchList.asStateFlow()

    fun loadSearchHistory() = viewModelScope.launch {
        getAllSearchHistoryUseCase().collect { history ->
            _searchHistory.value = history
        }
    }

    fun searchTracks(query: String) = viewModelScope.launch {
        runCatching { searchTracksUseCase(query) }
            .onSuccess { tracks ->
                _searchList.value = tracks.map { it.toTrackUiState() }
            }
            .onFailure {
                _searchList.value = emptyList()
            }
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
            artist = artists.joinToString(", ") { it.name },
        )
}