package com.stopstone.musicplaylist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.domain.usecase.splash.GetTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val getTokenUseCase: GetTokenUseCase,
    ) : ViewModel() {
        private val _token = MutableSharedFlow<String?>(replay = 1)
        val token: SharedFlow<String?> = _token.asSharedFlow()

        init {
            checkToken()
        }

        // 토큰 유효성 검사
        private fun checkToken() =
            viewModelScope.launch {
                runCatching {
                    getTokenUseCase()
                }.onSuccess { token ->
                    _token.emit(token)
                }.onFailure {
                    _token.emit(null)
                }
            }
    }
