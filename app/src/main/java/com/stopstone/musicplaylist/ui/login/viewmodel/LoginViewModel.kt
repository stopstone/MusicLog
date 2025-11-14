package com.stopstone.musicplaylist.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.data.model.dto.UserProfileDto
import com.stopstone.musicplaylist.domain.usecase.emotion_setting.SyncEmotionSettingsUseCase
import com.stopstone.musicplaylist.domain.usecase.insta_share.SyncInstagramShareSettingUseCase
import com.stopstone.musicplaylist.domain.usecase.login.GetUserIdUseCase
import com.stopstone.musicplaylist.domain.usecase.login.SaveUserIdUseCase
import com.stopstone.musicplaylist.domain.usecase.login.SyncMusicFromFirestoreUseCase
import com.stopstone.musicplaylist.domain.usecase.login.SyncSignatureSongFromFirestoreUseCase
import com.stopstone.musicplaylist.domain.usecase.splash.GetTokenUseCase
import com.stopstone.musicplaylist.domain.usecase.user.ResetLocalUserCacheUseCase
import com.stopstone.musicplaylist.domain.usecase.user.SaveUserProfileUseCase
import com.stopstone.musicplaylist.ui.login.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val getUserIdUseCase: GetUserIdUseCase,
        private val saveUserIdUseCase: SaveUserIdUseCase,
        private val syncMusicFromFirestoreUseCase: SyncMusicFromFirestoreUseCase,
        private val syncSignatureSongFromFirestoreUseCase: SyncSignatureSongFromFirestoreUseCase,
        private val syncInstagramShareSettingUseCase: SyncInstagramShareSettingUseCase,
        private val syncEmotionSettingsUseCase: SyncEmotionSettingsUseCase,
        private val getTokenUseCase: GetTokenUseCase,
        private val saveUserProfileUseCase: SaveUserProfileUseCase,
        private val resetLocalUserCacheUseCase: ResetLocalUserCacheUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
        val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

        fun checkAutoLogin() {
            viewModelScope.launch {
                try {
                    _uiState.value = LoginUiState.Loading
                    val userId = getUserIdUseCase()
                    if (userId != null) {
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

        fun onLoginSuccess(userProfile: UserProfile) {
            viewModelScope.launch {
                try {
                    _uiState.value = LoginUiState.Loading
                    val previousUserId = getUserIdUseCase()
                    if (previousUserId == null || previousUserId != userProfile.userId) {
                        // 사용자 전환 시 로컬 캐시를 먼저 정리해 동기화 시 충돌을 방지
                        resetLocalUserCacheUseCase()
                    }
                    saveUserIdUseCase(userProfile.userId)
                    val profileDto =
                        UserProfileDto(
                            email = userProfile.email,
                            displayName = userProfile.displayName,
                            photoUrl = userProfile.photoUrl,
                            createdAt = Date(),
                            providerType = userProfile.providerType.name,
                        )
                    saveUserProfileUseCase(userProfile.userId, profileDto)
                    val musicSyncResult = syncMusicFromFirestoreUseCase(userProfile.userId)
                    val signatureSongSyncResult = syncSignatureSongFromFirestoreUseCase(userProfile.userId)
                    val instagramSettingSyncResult = syncInstagramShareSettingUseCase(userProfile.userId)
                    val emotionSettingSyncResult = syncEmotionSettingsUseCase.execute(userProfile.userId)

                    if (
                        musicSyncResult.isSuccess &&
                        signatureSongSyncResult.isSuccess &&
                        instagramSettingSyncResult.isSuccess &&
                        emotionSettingSyncResult.isSuccess
                    ) {
                        _uiState.value = LoginUiState.Success
                    } else {
                        _uiState.value = LoginUiState.SuccessWithSyncError
                    }
                } catch (e: Exception) {
                    _uiState.value = LoginUiState.Error(e.message)
                }
            }
        }

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
    data class Error(
        val message: String?,
    ) : LoginUiState()
}
