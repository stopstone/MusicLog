package com.stopstone.musicplaylist.ui.login.model

import androidx.annotation.DrawableRes
import com.stopstone.musicplaylist.R

enum class ProviderType(
    val displayName: String,
    val isVisible: Boolean,
    @DrawableRes val badgeIconRes: Int,
) {
    KAKAO("카카오", true, R.drawable.ic_kakao_badge_32),
    NONE("", false, 0),
}
