package com.stopstone.musicplaylist.data.local.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val authService: AuthService
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 토큰 요청 API는 인터셉트하지 않음
        if (originalRequest.url.toString().contains("api/token")) {
            return chain.proceed(originalRequest)
        }

        return runBlocking {
            // 토큰이 만료되었는지 확인
            if (tokenManager.isTokenExpired()) {
                authService.refreshToken()
            }

            val accessToken = tokenManager.getToken().first()
            if (accessToken.isNullOrEmpty()) {
                authService.refreshToken()
            }

            // 새로운 토큰으로 요청
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer ${tokenManager.getToken().first()}")
                .build()

            chain.proceed(newRequest)
        }
    }
}