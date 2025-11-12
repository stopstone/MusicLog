package com.stopstone.musicplaylist.data.repository.my

import android.util.Log
import com.stopstone.musicplaylist.data.local.auth.UserPreferences
import com.stopstone.musicplaylist.data.local.dao.SignatureSongDao
import com.stopstone.musicplaylist.data.model.entity.SignatureSong
import com.stopstone.musicplaylist.data.remote.datasource.FirestoreDataSource
import com.stopstone.musicplaylist.domain.repository.my.SignatureSongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignatureSongRepositoryImpl
    @Inject
    constructor(
        private val signatureSongDao: SignatureSongDao,
        private val firestoreDataSource: FirestoreDataSource,
        private val userPreferences: UserPreferences,
    ) : SignatureSongRepository {
        override suspend fun setSignatureSong(signatureSong: SignatureSong) {
            signatureSongDao.setNewSignatureSong(signatureSong)

            userPreferences.getUserIdSync()?.let { userId ->
                val result = firestoreDataSource.saveSignatureSong(userId, signatureSong)
                result.onFailure { exception ->
                    Log.e(TAG, "Failed to save signature song to Firestore", exception)
                }
            }
        }

        override fun getActiveSignatureSong(): Flow<SignatureSong?> = signatureSongDao.getActiveSignatureSong()

        override suspend fun getActiveSignatureSongOnce(): SignatureSong? = signatureSongDao.getActiveSignatureSongOnce()

        override fun getAllSignatureSongs(): Flow<List<SignatureSong>> = signatureSongDao.getAllSignatureSongs()

        override suspend fun getAllSignatureSongsOnce(): List<SignatureSong> = signatureSongDao.getAllSignatureSongsOnce()

        override suspend fun deleteAllSignatureSongs() {
            signatureSongDao.deleteAllSignatureSongs()

            userPreferences.getUserIdSync()?.let { userId ->
                val result = firestoreDataSource.deleteAllSignatureSongs(userId)
                result.onFailure { exception ->
                    Log.e(TAG, "Failed to delete signature songs from Firestore", exception)
                }
            }
        }

        override suspend fun syncFromFirestore(userId: String): Result<Unit> =
            try {
                val result = firestoreDataSource.getAllSignatureSongs(userId)
                val firebaseSignatureSongs = result.getOrNull() ?: emptyList()

                firebaseSignatureSongs.forEach { signatureSong ->
                    signatureSongDao.insert(signatureSong)
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }

        companion object {
            private const val TAG = "SignatureSongRepoImpl"
        }
    }
