package com.stopstone.musicplaylist.data.repository.common

import com.stopstone.musicplaylist.BuildConfig
import com.stopstone.musicplaylist.data.local.auth.TokenManager
import com.stopstone.musicplaylist.data.remote.api.SpotifyAuthApi
import com.stopstone.musicplaylist.domain.repository.common.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val spotifyAuthApi: SpotifyAuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun getToken(): String = try {
        val storedToken = tokenManager.getToken().first()
        storedToken.takeIf { it != null && !isTokenExpired() } ?: refreshToken() // false일때 null
    } catch (e: Exception) {
        throw e
    }

    override suspend fun refreshToken(): String = try {
        clearTokenData()
        val response = spotifyAuthApi.getToken(
            GRANT_TYPE,
            BuildConfig.CLIENT_ID,
            BuildConfig.CLIENT_SECRET
        )
        val newToken = response.accessToken
        val newExpirationTime = System.currentTimeMillis() + response.expiresIn * 1000

        tokenManager.saveToken(newToken)
        tokenManager.saveTokenExpireTime(newExpirationTime)
        newToken
    } catch (e: Exception) {
        throw e
    }

    override suspend fun clearTokenData() = tokenManager.clearAll()

    override suspend fun isTokenExpired(): Boolean {
        val expirationTime = tokenManager.getTokenExpireTime().first() ?: 0L
        return System.currentTimeMillis() > expirationTime
    }

    companion object {
        private const val GRANT_TYPE = "client_credentials"
    }
}

