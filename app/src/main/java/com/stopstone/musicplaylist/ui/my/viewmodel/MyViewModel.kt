package com.stopstone.musicplaylist.ui.my.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.domain.usecase.my.GetMusicCountUseCase
import com.stopstone.musicplaylist.ui.my.model.MyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel
    @Inject
    constructor(
        private val getMusicCountUseCase: GetMusicCountUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(MyUiState())
        val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()

        fun loadMusicCount() =
            viewModelScope.launch {
                try {
                    _uiState.update { it.copy(isLoading = true) }
                    val count = getMusicCountUseCase()
                    _uiState.update {
                        it.copy(
                            musicCount = count,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message,
                        )
                    }
                }
            }
    }

