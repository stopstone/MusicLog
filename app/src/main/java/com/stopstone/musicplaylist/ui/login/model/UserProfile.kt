package com.stopstone.musicplaylist.ui.login.model

data class UserProfile(
    val userId: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val providerType: ProviderType,
)

