package com.stopstone.musicplaylist.ui.my.model

import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.ui.insta_share_setting.InstagramShareSettingActivity

enum class MySettingMenu(
    val title: String,
    val destinationId: Int = 0,
) {
    ACCOUNT("계정 설정", R.id.navigation_user_setting),
    EMOTION_TAG("감정 태그 관리"),
    NOTIFICATION("알림 설정"),
    INSTAGRAM_SHARE("인스타 공유 설정", R.id.navigation_insta_share_setting),
}
