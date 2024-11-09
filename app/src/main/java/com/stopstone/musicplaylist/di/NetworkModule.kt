package com.stopstone.musicplaylist.di

import com.stopstone.musicplaylist.data.remote.api.SpotifyApi
import com.stopstone.musicplaylist.data.remote.api.SpotifyAuthApi
import com.stopstone.musicplaylist.data.local.auth.TokenManager
import com.stopstone.musicplaylist.data.local.auth.AuthInterceptor
import com.stopstone.musicplaylist.data.local.auth.AuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val AUTH_BASE_URL = "https://accounts.spotify.com/"
    private const val API_BASE_URL = "https://api.spotify.com/"

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenManager: TokenManager,
        authService: AuthService
    ): AuthInterceptor {
        return AuthInterceptor(tokenManager, authService)
    }

    @Provides
    @Singleton
    @BaseHttpClient
    fun provideBaseOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    @AuthHttpClient
    fun provideAuthOkHttpClient(
        @BaseHttpClient baseClient: OkHttpClient
    ): OkHttpClient {
        return baseClient.newBuilder().build()
    }

    @Provides
    @Singleton
    @ApiHttpClient
    fun provideApiOkHttpClient(
        @BaseHttpClient baseClient: OkHttpClient,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return baseClient.newBuilder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(
        tokenManager: TokenManager,
        @BaseHttpClient baseClient: OkHttpClient
    ): AuthService {
        val authApi = provideRetrofit(AUTH_BASE_URL, baseClient)
            .create(SpotifyAuthApi::class.java)
        return AuthService(authApi, tokenManager)
    }

    private fun provideRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSpotifyAuthApi(
        @AuthHttpClient authClient: OkHttpClient
    ): SpotifyAuthApi {
        return provideRetrofit(AUTH_BASE_URL, authClient)
            .create(SpotifyAuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSpotifyApi(
        @ApiHttpClient apiClient: OkHttpClient
    ): SpotifyApi {
        return provideRetrofit(API_BASE_URL, apiClient)
            .create(SpotifyApi::class.java)
    }
}