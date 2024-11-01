package com.stopstone.musicplaylist.domain.repository.common

interface AuthRepository {
    suspend fun getToken(): String
    suspend fun isTokenExpired(): Boolean
    suspend fun clearTokenData()
    suspend fun refreshToken(): String
}