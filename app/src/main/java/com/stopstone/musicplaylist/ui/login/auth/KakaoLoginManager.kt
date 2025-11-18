package com.stopstone.musicplaylist.ui.login.auth

import android.content.Context
import com.kakao.sdk.common.model.ApiError
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.ui.login.model.ProviderType
import com.stopstone.musicplaylist.ui.login.model.UserProfile
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * 카카오 로그인을 담당하는 매니저 클래스
 */
class KakaoLoginManager(
    private val context: Context,
) : SocialLoginProvider {
    /**
     * 카카오 로그인 수행
     * KakaoTalk이 설치되어 있으면 KakaoTalk으로, 아니면 웹 계정으로 로그인합니다.
     */
    override suspend fun performLogin(
        onSuccess: (UserProfile) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        try {
            executeLogin()
            val userProfile = getUserInfo()
            onSuccess(userProfile)
        } catch (error: AuthError) {
            onFailure(Exception(context.getString(R.string.login_auth_error)))
        } catch (error: ApiError) {
            onFailure(Exception(context.getString(R.string.login_api_error)))
        } catch (error: ClientError) {
            onFailure(Exception(context.getString(R.string.login_client_error)))
        } catch (error: KakaoSdkError) {
            onFailure(Exception(context.getString(R.string.login_sdk_error)))
        } catch (exception: Exception) {
            onFailure(exception)
        }
    }

    private suspend fun executeLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            try {
                loginWithKakaoTalk()
                return
            } catch (error: Throwable) {
                if (error.isUserCancelled()) {
                    throw error
                }
            }
        }
        loginWithKakaoAccount()
    }

    private suspend fun getUserInfo(): UserProfile =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.me { user, error ->
                when {
                    error != null -> {
                        continuation.resumeWith(
                            Result.failure(
                                Exception(context.getString(R.string.login_api_error)),
                            ),
                        )
                    }

                    user != null -> {
                        val userProfile =
                            UserProfile(
                                userId = user.id.toString(),
                                email = user.kakaoAccount?.email.orEmpty(),
                                displayName =
                                    user.kakaoAccount
                                        ?.profile
                                        ?.nickname
                                        .orEmpty(),
                                photoUrl = user.kakaoAccount?.profile?.profileImageUrl,
                                providerType = ProviderType.KAKAO,
                            )
                        continuation.resume(userProfile)
                    }

                    else -> {
                        continuation.resumeWith(
                            Result.failure(
                                Exception(context.getString(R.string.login_api_error)),
                            ),
                        )
                    }
                }
            }
        }

    private suspend fun loginWithKakaoTalk(): Unit =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                when {
                    error != null -> continuation.resumeWith(Result.failure(error))
                    token != null -> continuation.resume(Unit)
                    else -> continuation.resumeWith(Result.failure(IllegalStateException(context.getString(R.string.login_user_cancelled))))
                }
            }
        }

    private suspend fun loginWithKakaoAccount(): Unit =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                when {
                    error != null -> continuation.resumeWith(Result.failure(error))
                    token != null -> continuation.resume(Unit)
                    else -> continuation.resumeWith(Result.failure(IllegalStateException(context.getString(R.string.login_user_cancelled))))
                }
            }
        }

    private fun Throwable.isUserCancelled(): Boolean {
        if (this is ClientError && reason == ClientErrorCause.Cancelled) {
            return true
        }
        if (this is AuthError && this.statusCode == 302) {
            return true
        }
        return false
    }
}
