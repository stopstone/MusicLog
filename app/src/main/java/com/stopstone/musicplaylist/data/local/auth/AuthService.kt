package com.stopstone.musicplaylist.data.local.auth

import com.stopstone.musicplaylist.BuildConfig
import com.stopstone.musicplaylist.data.remote.api.SpotifyAuthApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService
    @Inject
    constructor(
        private val spotifyAuthApi: SpotifyAuthApi,
        private val tokenManager: TokenManager,
    ) {
        suspend fun refreshToken() {
            try {
                val response =
                    spotifyAuthApi.getToken(
                        grantType = "client_credentials",
                        clientId = BuildConfig.CLIENT_ID,
                        clientSecret = BuildConfig.CLIENT_SECRET,
                    )
                tokenManager.saveToken(response.accessToken)
            } catch (e: Exception) {
                tokenManager.clearToken()
                throw e
            }
        }
    }
