package com.stopstone.musicplaylist.ui.login.auth

import com.stopstone.musicplaylist.ui.login.model.UserProfile

// 소셜 로그인 제공자가 구현해야 하는 인터페이스
interface SocialLoginProvider {
    suspend fun performLogin(
        onSuccess: (UserProfile) -> Unit,
        onFailure: (Throwable) -> Unit,
    )
}
