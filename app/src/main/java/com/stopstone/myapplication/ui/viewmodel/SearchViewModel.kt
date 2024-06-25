package com.stopstone.myapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.data.model.Track
import com.stopstone.myapplication.data.repository.SearchRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: SearchRepository): ViewModel() {
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    fun searchTracks(query: String) = viewModelScope.launch {
        val tracks = repository.searchTracks(query)
        _tracks.value = tracks
    }
}