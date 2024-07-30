package com.stopstone.myapplication.di

import com.stopstone.myapplication.data.api.SpotifyApi
import com.stopstone.myapplication.data.api.SpotifyAuthApi
import com.stopstone.myapplication.data.local.TokenManager
import com.stopstone.myapplication.util.AuthInterceptor
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
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .build()
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
    fun provideSpotifyAuthApi(okHttpClient: OkHttpClient): SpotifyAuthApi {
        return provideRetrofit(AUTH_BASE_URL, okHttpClient).create(SpotifyAuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSpotifyApi(okHttpClient: OkHttpClient): SpotifyApi {
        return provideRetrofit(API_BASE_URL, okHttpClient).create(SpotifyApi::class.java)
    }
}
