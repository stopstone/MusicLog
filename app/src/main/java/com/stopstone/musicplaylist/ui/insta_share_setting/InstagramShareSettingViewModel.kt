package com.stopstone.musicplaylist.ui.insta_share_setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.domain.usecase.insta_share.GetInstagramShareSettingUseCase
import com.stopstone.musicplaylist.domain.usecase.insta_share.UpdateInstagramShareSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstagramShareSettingViewModel
    @Inject
    constructor(
        private val getInstagramShareSettingUseCase: GetInstagramShareSettingUseCase,
        private val updateInstagramShareSettingUseCase: UpdateInstagramShareSettingUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(InstagramShareSettingUiState())
        val uiState: StateFlow<InstagramShareSettingUiState> = _uiState.asStateFlow()

        init {
            loadSettings()
        }

        private fun loadSettings() {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoadingFromDataStore = true)
                getInstagramShareSettingUseCase().collect { setting ->
                    _uiState.value =
                        InstagramShareSettingUiState(
                            showEmotions = setting.showEmotions,
                            showMemo = setting.showMemo,
                            isLoadingFromDataStore = false,
                        )
                }
            }
        }

        fun updateShowEmotions(show: Boolean) {
            viewModelScope.launch {
                updateInstagramShareSettingUseCase.setShowEmotions(show)
            }
        }

        fun updateShowMemo(show: Boolean) {
            viewModelScope.launch {
                updateInstagramShareSettingUseCase.setShowMemo(show)
            }
        }
    }

data class InstagramShareSettingUiState(
    val showEmotions: Boolean = false,
    val showMemo: Boolean = false,
    val isLoadingFromDataStore: Boolean = true,
)
