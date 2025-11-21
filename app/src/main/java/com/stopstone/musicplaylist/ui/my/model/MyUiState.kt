package com.stopstone.musicplaylist.ui.my.model

data class MyUiState(
    val musicCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isDailyReminderEnabled: Boolean = false,
)

