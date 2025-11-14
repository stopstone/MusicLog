package com.stopstone.musicplaylist.data.repository.insta_share

import com.stopstone.musicplaylist.data.local.auth.UserPreferences
import com.stopstone.musicplaylist.data.local.settings.InstagramSharePreferences
import com.stopstone.musicplaylist.data.remote.datasource.FirestoreDataSource
import com.stopstone.musicplaylist.data.remote.dto.InstagramShareSettingDto
import com.stopstone.musicplaylist.domain.repository.insta_share.InstagramShareSettingRepository
import com.stopstone.musicplaylist.domain.usecase.insta_share.InstagramShareSetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstagramShareSettingRepositoryImpl
    @Inject
    constructor(
        private val instagramSharePreferences: InstagramSharePreferences,
        private val firestoreDataSource: FirestoreDataSource,
        private val userPreferences: UserPreferences,
    ) : InstagramShareSettingRepository {
        override fun getSettings(): Flow<InstagramShareSetting> =
            combine(
                instagramSharePreferences.getShowEmotions(),
                instagramSharePreferences.getShowMemo(),
            ) { showEmotions, showMemo ->
                InstagramShareSetting(
                    showEmotions = showEmotions,
                    showMemo = showMemo,
                )
            }

        override suspend fun setShowEmotions(show: Boolean) {
            // 1. DataStore에 저장
            instagramSharePreferences.setShowEmotions(show)
            // 2. Firebase에 저장
            val userId = userPreferences.getUserId().first()
            if (userId?.isNotEmpty() == true) {
                val currentSettings = getSettings().first()
                val settingDto =
                    InstagramShareSettingDto(
                        showEmotions = show,
                        showMemo = currentSettings.showMemo,
                    )
                firestoreDataSource.saveInstagramShareSetting(userId, settingDto)
            }
        }

        override suspend fun setShowMemo(show: Boolean) {
            // 1. DataStore에 저장
            instagramSharePreferences.setShowMemo(show)
            // 2. Firebase에 저장
            val userId = userPreferences.getUserId().first()
            if (userId?.isNotEmpty() == true) {
                val currentSettings = getSettings().first()
                val settingDto =
                    InstagramShareSettingDto(
                        showEmotions = currentSettings.showEmotions,
                        showMemo = show,
                    )
                firestoreDataSource.saveInstagramShareSetting(userId, settingDto)
            }
        }

        override suspend fun syncRemoteSettings(userId: String): Result<Unit> {
            val remoteResult = firestoreDataSource.getInstagramShareSetting(userId)
            return remoteResult.fold(
                onSuccess = { dto ->
                    val showEmotions = dto?.showEmotions ?: false
                    val showMemo = dto?.showMemo ?: false
                    instagramSharePreferences.setShowEmotions(showEmotions)
                    instagramSharePreferences.setShowMemo(showMemo)
                    Result.success(Unit)
                },
                onFailure = { throwable ->
                    Result.failure(throwable)
                },
            )
        }

        override suspend fun clearLocalSettings() {
            instagramSharePreferences.clearSettings() // 인스타그램 공유 설정 DataStore 초기화
        }
    }
