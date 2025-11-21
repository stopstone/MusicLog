package com.stopstone.musicplaylist.ui.my.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.data.model.entity.SignatureSong
import com.stopstone.musicplaylist.domain.usecase.my.GetActiveSignatureSongUseCase
import com.stopstone.musicplaylist.domain.usecase.my.GetMusicCountUseCase
import com.stopstone.musicplaylist.domain.usecase.notification.ObserveDailyReminderEnabledUseCase
import com.stopstone.musicplaylist.domain.usecase.notification.SetDailyReminderEnabledUseCase
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
        private val getActiveSignatureSongUseCase: GetActiveSignatureSongUseCase,
        private val observeDailyReminderEnabledUseCase: ObserveDailyReminderEnabledUseCase,
        private val setDailyReminderEnabledUseCase: SetDailyReminderEnabledUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(MyUiState())
        val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()

        private val _signatureSong = MutableStateFlow<SignatureSong?>(null)
        val signatureSong: StateFlow<SignatureSong?> = _signatureSong.asStateFlow()

        init {
            observeDailyReminderSetting()
        }

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

        fun loadSignatureSong() =
            viewModelScope.launch {
                getActiveSignatureSongUseCase().collect { signatureSong ->
                    _signatureSong.value = signatureSong
                }
            }

        fun updateDailyReminderEnabled(enabled: Boolean) =
            viewModelScope.launch {
                setDailyReminderEnabledUseCase(enabled)
            }

        private fun observeDailyReminderSetting() =
            viewModelScope.launch {
                observeDailyReminderEnabledUseCase().collect { isEnabled ->
                    _uiState.update { it.copy(isDailyReminderEnabled = isEnabled) }
                }
            }
    }

