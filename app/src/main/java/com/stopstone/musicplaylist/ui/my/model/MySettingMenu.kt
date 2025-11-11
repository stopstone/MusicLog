package com.stopstone.musicplaylist.ui.my.model

import com.stopstone.musicplaylist.R

enum class MySettingMenu(
    val title: String,
    val destinationId: Int,
) {
    ACCOUNT("계정 설정", R.id.navigation_user_setting),
    EMOTION_TAG("감정 태그 관리", 0), // TODO: R.id.emotionTagFragment로 변경
    NOTIFICATION("알림 설정", 0), // TODO: R.id.notificationSettingFragment로 변경
    INSTAGRAM_SHARE("인스타 공유 설정", 0), // TODO: R.id.instagramShareSettingFragment로 변경
}
