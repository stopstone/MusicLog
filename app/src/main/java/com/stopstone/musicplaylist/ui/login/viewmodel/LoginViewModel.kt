package com.stopstone.musicplaylist.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.data.local.auth.UserPreferences
import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onLoginSuccess(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = LoginUiState.Loading
                userPreferences.saveUserId(userId)

                val result = trackRepository.syncFromFirestore(userId)

                if (result.isSuccess) {
                    _uiState.value = LoginUiState.Success
                } else {
                    _uiState.value = LoginUiState.SuccessWithSyncError
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
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
    
    // 로딩 중
    data object Loading : LoginUiState()
    
    // 로그인 성공
    data object Success : LoginUiState()
    
    // 로그인 성공 (동기화 실패)
    data object SuccessWithSyncError : LoginUiState()
    
    // 에러
    data class Error(val message: String) : LoginUiState()
}

