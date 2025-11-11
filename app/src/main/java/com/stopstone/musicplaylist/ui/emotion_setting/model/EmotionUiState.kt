package com.stopstone.musicplaylist.ui.emotion_setting.model

import com.stopstone.musicplaylist.base.BaseIdModel
import com.stopstone.musicplaylist.domain.model.Emotions

data class EmotionUiState(
    val emotion: Emotions,
    val displayName: String,
    val isSelected: Boolean = false,
) : BaseIdModel {
    override val id: Any
        get() = emotion.name
}
