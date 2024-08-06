package com.stopstone.myapplication.ui.splash.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.domain.usecase.splash.GetTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getTokenUseCase: GetTokenUseCase,
) : ViewModel() {

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    fun getToken() = viewModelScope.launch {
        runCatching { getTokenUseCase() }
            .onSuccess { token ->
                _token.value = token
                Log.d(TAG, "토큰을 성공적으로 가져왔습니다: $token")
            }
            .onFailure { e ->
                _token.value = null
                Log.e(TAG, "토큰 가져오기 실패", e)
            }
    }

    companion object {
        private const val TAG = "SplashViewModel"
    }
}