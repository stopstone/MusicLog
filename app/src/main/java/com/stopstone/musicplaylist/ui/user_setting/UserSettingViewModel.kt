package com.stopstone.musicplaylist.ui.user_setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.domain.usecase.login.GetUserIdUseCase
import com.stopstone.musicplaylist.domain.usecase.user.ClearUserDataUseCase
import com.stopstone.musicplaylist.domain.usecase.user.DeleteUserAccountUseCase
import com.stopstone.musicplaylist.domain.usecase.user.GetUserProfileUseCase
import com.stopstone.musicplaylist.domain.usecase.user.LogoutUseCase
import com.stopstone.musicplaylist.ui.login.model.ProviderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSettingViewModel
    @Inject
    constructor(
        private val getUserProfileUseCase: GetUserProfileUseCase,
        private val getUserIdUseCase: GetUserIdUseCase,
        private val logoutUseCase: LogoutUseCase,
        private val deleteUserAccountUseCase: DeleteUserAccountUseCase,
        private val clearUserDataUseCase: ClearUserDataUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(UserSettingUiState())
        val uiState: StateFlow<UserSettingUiState> = _uiState.asStateFlow()

        init {
            loadUserProfile()
        }

        fun loadUserProfile() {
            viewModelScope.launch {
                try {
                    _uiState.update { it.copy(isLoading = true) }
                    val userId = getUserIdUseCase()
                    if (userId != null) {
                        val result = getUserProfileUseCase(userId)
                        if (result.isSuccess) {
                            val profile = result.getOrNull()
                            val provider = profile?.providerType?.let {
                                try {
                                    ProviderType.valueOf(it)
                                } catch (e: Exception) {
                                    ProviderType.NONE
                                }
                            } ?: ProviderType.NONE
                            _uiState.update {
                                it.copy(
                                    email = profile?.email.orEmpty(),
                                    displayName = profile?.displayName.orEmpty(),
                                    photoUrl = profile?.photoUrl,
                                    providerType = provider,
                                    isLoading = false,
                                    errorMessage = null,
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.exceptionOrNull()?.message,
                                )
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "사용자 정보를 찾을 수 없습니다",
                            )
                        }
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

        fun logout() {
            viewModelScope.launch {
                try {
                    _uiState.update { it.copy(isLoading = true) }
                    logoutUseCase()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedOut = true,
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

        fun deleteAccount() {
            viewModelScope.launch {
                try {
                    _uiState.update { it.copy(isLoading = true) }
                    val userId = getUserIdUseCase()
                    if (userId != null) {
                        val result = deleteUserAccountUseCase(userId)
                        if (result.isSuccess) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isAccountDeleted = true,
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "회원 탈퇴에 실패했습니다",
                                )
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "사용자 정보를 찾을 수 없습니다",
                            )
                        }
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

        fun clearUserData() {
            viewModelScope.launch {
                try {
                    _uiState.update { it.copy(isLoading = true) }
                    val userId = getUserIdUseCase()
                    if (userId != null) {
                        val result = clearUserDataUseCase(userId)
                        if (result.isSuccess) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isDataCleared = true,
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "데이터 초기화에 실패했습니다",
                                )
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "사용자 정보를 찾을 수 없습니다",
                            )
                        }
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
    }

data class UserSettingUiState(
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val providerType: ProviderType = ProviderType.NONE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAccountDeleted: Boolean = false,
    val isDataCleared: Boolean = false,
    val isLoggedOut: Boolean = false,
)
