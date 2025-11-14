package com.stopstone.musicplaylist.ui.signature_list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.domain.usecase.my.GetAllSignatureSongsUseCase
import com.stopstone.musicplaylist.ui.signature_list.model.SignatureListUiState
import com.stopstone.musicplaylist.ui.signature_list.model.toHistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignatureListViewModel
    @Inject
    constructor(
        private val getAllSignatureSongsUseCase: GetAllSignatureSongsUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(SignatureListUiState())
        val uiState: StateFlow<SignatureListUiState> = _uiState.asStateFlow()

        init {
            observeSignatureSongs()
        }

        private fun observeSignatureSongs() {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                getAllSignatureSongsUseCase()
                    .catch { throwable ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = throwable.message ?: "Failed to load signature songs.",
                            )
                        }
                    }.collect { songs ->
                        val uiSongs = songs.map { it.toHistoryUiState() }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                songs = uiSongs,
                                errorMessage = null,
                            )
                        }
                    }
            }
        }
    }
