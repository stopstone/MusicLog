package com.stopstone.myapplication.domain.repository

interface AuthRepository {
    suspend fun getToken(): Result<String>
    suspend fun clearTokenData()
    suspend fun isTokenExpired(): Boolean
}