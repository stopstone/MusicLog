package com.stopstone.myapplication.data.repository

import android.util.Log
import com.stopstone.myapplication.BuildConfig
import com.stopstone.myapplication.data.remote.api.SpotifyAuthApi
import com.stopstone.myapplication.data.local.auth.TokenManager
import com.stopstone.myapplication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val spotifyAuthApi: SpotifyAuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {
    private val TEST_EXPIRATION_TIME = 10 * 1000L

    override suspend fun getToken(): Result<String> = try {
        val storedToken = tokenManager.getToken().first()
        if (storedToken != null && !isTokenExpired()) {
            Log.d(TAG, "저장된 토큰 사용: $storedToken")
            Result.success(storedToken)
        } else {
            Log.d(TAG, "토큰이 만료되었거나 없습니다. 토큰을 새로 고칩니다.")
            refreshToken()
        }
    } catch (e: Exception) {
        Log.e(TAG, "토큰 가져오기 오류", e)
        Result.failure(e)
    }

    private suspend fun refreshToken(): Result<String> = try {
        clearTokenData()
        val response = spotifyAuthApi.getToken(
            "client_credentials",
            BuildConfig.CLIENT_ID,
            BuildConfig.CLIENT_SECRET
        )
        val newToken = response.accessToken
        val newExpirationTime = System.currentTimeMillis() + TEST_EXPIRATION_TIME

        tokenManager.saveToken(newToken)
        tokenManager.saveTokenExpireTime(newExpirationTime)

        Log.d(TAG, "새 토큰: $newToken")
        Log.d(TAG, "새 만료 시간: $newExpirationTime")
        Result.success(newToken)
    } catch (e: Exception) {
        Log.e(TAG, "토큰 새로 고침 오류", e)
        Result.failure(e)
    }

    override suspend fun clearTokenData() {
        Log.d(TAG, "토큰 데이터 초기화")
        tokenManager.clearAll()
    }

    override suspend fun isTokenExpired(): Boolean {
        val expirationTime = tokenManager.getTokenExpireTime().first() ?: 0L
        val isExpired = System.currentTimeMillis() > expirationTime
        Log.d(TAG, "토큰 만료 여부: $isExpired")
        return isExpired
    }

    companion object {
        private const val TAG = "AuthRepository"
    }
}
