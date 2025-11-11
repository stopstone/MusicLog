package com.stopstone.musicplaylist.ui.my.model

import androidx.annotation.StringRes
import com.stopstone.musicplaylist.R

enum class MySettingMenu(
    @StringRes val titleRes: Int,
    val destinationId: Int = 0,
) {
    ACCOUNT(R.string.label_my_menu_account_setting, R.id.navigation_user_setting),
    EMOTION_TAG(R.string.label_my_menu_emotion_tag),

    //    NOTIFICATION(R.string.label_my_menu_notification),
    INSTAGRAM_SHARE(R.string.label_my_menu_instagram_share, R.id.navigation_insta_share_setting),
}
