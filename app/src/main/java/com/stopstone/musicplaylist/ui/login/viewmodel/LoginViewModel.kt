package com.stopstone.musicplaylist.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.domain.usecase.login.GetUserIdUseCase
import com.stopstone.musicplaylist.domain.usecase.login.SaveUserIdUseCase
import com.stopstone.musicplaylist.domain.usecase.login.SyncMusicFromFirestoreUseCase
import com.stopstone.musicplaylist.domain.usecase.splash.GetTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val saveUserIdUseCase: SaveUserIdUseCase,
    private val syncMusicFromFirestoreUseCase: SyncMusicFromFirestoreUseCase,
    private val getTokenUseCase: GetTokenUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun checkAutoLogin() {
        viewModelScope.launch {
            try {
                _uiState.value = LoginUiState.Loading
                
                // 1. userId 체크
                val userId = getUserIdUseCase()
                if (userId != null) {
                    // 2. Spotify 토큰 미리 가져오기 (캐싱)
                    getTokenUseCase()
                    _uiState.value = LoginUiState.AutoLoginSuccess
                } else {
                    _uiState.value = LoginUiState.ShowLoginScreen
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.ShowLoginScreen
            }
        }
    }

    fun onLoginSuccess(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = LoginUiState.Loading
                saveUserIdUseCase(userId)
                val result = syncMusicFromFirestoreUseCase(userId)

                if (result.isSuccess) {
                    _uiState.value = LoginUiState.Success
                } else {
                    _uiState.value = LoginUiState.SuccessWithSyncError
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message)
            }
        }
    }

    /**
     * 로그인 실패 시 처리
     */
    fun onLoginFailure(errorMessage: String) {
        _uiState.value = LoginUiState.Error(errorMessage)
    }

}

/**
 * 로그인 화면 UI 상태
 */
sealed class LoginUiState {
    // 초기 상태
    data object Idle : LoginUiState()
    
    // 자동 로그인 성공 (userId 있음)
    data object AutoLoginSuccess : LoginUiState()
    
    // 로그인 화면 표시 (userId 없음)
    data object ShowLoginScreen : LoginUiState()
    
    // 로딩 중
    data object Loading : LoginUiState()
    
    // 로그인 성공
    data object Success : LoginUiState()
    
    // 로그인 성공 (동기화 실패)
    data object SuccessWithSyncError : LoginUiState()
    
    // 에러
    data class Error(val message: String?) : LoginUiState()
}

