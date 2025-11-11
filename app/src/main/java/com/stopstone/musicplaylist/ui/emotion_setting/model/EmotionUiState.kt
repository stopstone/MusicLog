package com.stopstone.musicplaylist.ui.emotion_setting.model

import com.stopstone.musicplaylist.base.BaseIdModel
import com.stopstone.musicplaylist.domain.model.Emotions

data class EmotionUiState(
    val emotionId: String, // Emotions.name 또는 커스텀 감정 ID
    val displayName: String,
    val isCustom: Boolean = false, // 커스텀 감정 여부
    val isHidden: Boolean = false, // 숨김 처리 여부
    val order: Int = 0, // 정렬 순서
    val emotion: Emotions? = null, // 기본 감정인 경우에만 값이 있음
    val isSelectedForDelete: Boolean = false, // 삭제를 위해 선택된 상태
) : BaseIdModel {
    override val id: Any
        get() = emotionId

    // 삭제 가능 여부 (커스텀 감정만 삭제 가능)
    val isDeletable: Boolean
        get() = isCustom
}
