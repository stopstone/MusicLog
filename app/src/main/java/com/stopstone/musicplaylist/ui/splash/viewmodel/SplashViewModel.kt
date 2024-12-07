package com.stopstone.musicplaylist.ui.splash.viewmodel

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
class SplashViewModel @Inject constructor(
    private val getTokenUseCase: GetTokenUseCase,
) : ViewModel() {
    private val _token = MutableSharedFlow<String?>()
    val token: SharedFlow<String?> = _token.asSharedFlow()

    init {
        getToken()
    }

    private fun getToken() = viewModelScope.launch {
        runCatching { getTokenUseCase() }
            .onSuccess { token ->
                _token.emit(token)
            }
            .onFailure {
                _token.emit(null)
            }
    }

}