package com.stopstone.musicplaylist.data.model.dto

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class UserProfileDto(
    @PropertyName("email")
    val email: String = "",
    @PropertyName("displayName")
    val displayName: String = "",
    @PropertyName("photoUrl")
    val photoUrl: String? = null,
    @PropertyName("createdAt")
    val createdAt: Date = Date(),
    @PropertyName("providerType")
    val providerType: String = "",
)

