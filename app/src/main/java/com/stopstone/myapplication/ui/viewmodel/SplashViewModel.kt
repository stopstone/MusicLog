package com.stopstone.myapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.domain.usecase.ClearTokenUseCase
import com.stopstone.myapplication.domain.usecase.GetTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getTokenUseCase: GetTokenUseCase,
    private val clearTokenUseCase: ClearTokenUseCase,
) : ViewModel() {
    private val _tokenState = MutableStateFlow<TokenState>(TokenState.Initial)
    val tokenState: StateFlow<TokenState> = _tokenState.asStateFlow()

    init {
        getToken()
    }

    private fun getToken() = viewModelScope.launch {
        getTokenUseCase().fold(
            onSuccess = { token ->
                _tokenState.value = TokenState.Success(token)
                Log.d("SplashViewModel", "토큰을 성공적으로 가져왔습니다: $token")
            },
            onFailure = { exception ->
                val errorMessage = exception.message ?: "토큰 가져오기 실패"
                _tokenState.value = TokenState.Error(errorMessage)
                Log.e("SplashViewModel", "토큰 가져오기 실패", exception)
            }
        )
    }

    fun clearToken() = viewModelScope.launch {
        try {
            clearTokenUseCase()
            Log.d("SplashViewModel", "토큰이 성공적으로 초기화되었습니다")
            _tokenState.value = TokenState.Initial
            getToken() // 토큰을 지운 후 새로운 토큰을 가져오기
        } catch (e: Exception) {
            Log.e("SplashViewModel", "토큰 초기화 실패", e)
            _tokenState.value = TokenState.Error(e.message ?: "토큰 초기화 실패")
        }
    }
}

sealed class TokenState {
    object Initial : TokenState()
    data class Success(val token: String) : TokenState()
    data class Error(val message: String) : TokenState()
}