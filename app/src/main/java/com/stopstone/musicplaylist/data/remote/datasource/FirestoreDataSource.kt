package com.stopstone.musicplaylist.data.remote.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.stopstone.musicplaylist.data.model.dto.MusicDto
import com.stopstone.musicplaylist.data.model.dto.UserProfileDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
    ) {
        companion object {
            private const val COLLECTION_USERS = "users"
            private const val COLLECTION_MUSICS = "musics"
        private const val DOCUMENT_PROFILE = "profile"
        }

    suspend fun loadAllMusics(userId: String): Result<List<MusicDto>> {
        return try {
            val snapshot = firestore
                        .collection(COLLECTION_USERS)
                        .document(userId)
                        .collection(COLLECTION_MUSICS)
                        .get()
                        .await()

            val musics = snapshot.documents.mapNotNull { document ->
                        val musicDto = document.toObject(MusicDto::class.java)
                        musicDto
                    }

                Result.success(musics)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }

    suspend fun saveMusic(userId: String, musicDto: MusicDto): Result<Unit> {
        return try {
                val musicId = musicDto.date.time.toString()

                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_MUSICS)
                    .document(musicId)
                    .set(musicDto, SetOptions.merge())
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }

    suspend fun deleteMusic(userId: String, musicId: String): Result<Unit> {
        return try {
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_MUSICS)
                    .document(musicId)
                    .delete()
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }

    suspend fun deleteAllMusics(userId: String): Result<Unit> {
        return try {
            val snapshot = firestore
                        .collection(COLLECTION_USERS)
                        .document(userId)
                        .collection(COLLECTION_MUSICS)
                        .get()
                        .await()

                val batch = firestore.batch()
                snapshot.documents.forEach { document ->
                    batch.delete(document.reference)
                }
                batch.commit().await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }

    suspend fun updateComment(userId: String, musicId: String, comment: String): Result<Unit> {
        return try {
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_MUSICS)
                    .document(musicId)
                    .update("comment", comment)
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }

    suspend fun saveUserProfile(userId: String, profileDto: UserProfileDto): Result<Unit> {
        return try {
                    firestore
                        .collection(COLLECTION_USERS)
                        .document(userId)
                .collection(DOCUMENT_PROFILE)
                .document(DOCUMENT_PROFILE)
                .set(profileDto, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): Result<UserProfileDto?> {
        return try {
            val snapshot = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(DOCUMENT_PROFILE)
                .document(DOCUMENT_PROFILE)
                        .get()
                        .await()

            val profile = snapshot.toObject(UserProfileDto::class.java)
            Result.success(profile)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }

    suspend fun deleteUserAccount(userId: String): Result<Unit> {
        return try {
            // 1. musics subcollection 모든 데이터 삭제
            deleteAllMusics(userId)
            // 2. profile subcollection 삭제
            firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(DOCUMENT_PROFILE)
                .document(DOCUMENT_PROFILE)
                .delete()
                .await()
            // 3. user document 삭제
            firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

