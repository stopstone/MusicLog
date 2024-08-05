package com.stopstone.myapplication.domain.repository.common

interface AuthRepository {
    suspend fun getToken(): Result<String>
    suspend fun clearTokenData()
    suspend fun isTokenExpired(): Boolean
}