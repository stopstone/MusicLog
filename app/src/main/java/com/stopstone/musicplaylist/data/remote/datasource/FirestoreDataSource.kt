package com.stopstone.musicplaylist.data.remote.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.stopstone.musicplaylist.data.model.dto.MusicDto
import com.stopstone.musicplaylist.data.model.dto.UserProfileDto
import com.stopstone.musicplaylist.data.model.entity.SignatureSong
import com.stopstone.musicplaylist.data.remote.dto.InstagramShareSettingDto
import com.stopstone.musicplaylist.data.remote.dto.SignatureSongRemoteDto
import com.stopstone.musicplaylist.data.remote.dto.toEntity
import com.stopstone.musicplaylist.data.remote.dto.toRemoteDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) {
        companion object {
            private const val COLLECTION_USERS = "users"
            private const val COLLECTION_MUSICS = "musics"
            private const val DOCUMENT_PROFILE = "profile"
            private const val COLLECTION_SETTINGS = "settings"
            private const val DOCUMENT_INSTAGRAM_SHARE = "instagram_share"
            private const val COLLECTION_SIGNATURE_SONGS = "signature_songs"
        }

        suspend fun loadAllMusics(userId: String): Result<List<MusicDto>> =
            try {
                val snapshot =
                    firestore
                        .collection(COLLECTION_USERS)
                        .document(userId)
                        .collection(COLLECTION_MUSICS)
                        .get()
                        .await()

                val musics =
                    snapshot.documents.mapNotNull { document ->
                        val musicDto = document.toObject(MusicDto::class.java)
                        musicDto
                    }

                Result.success(musics)
            } catch (e: Exception) {
                Result.failure(e)
            }

        suspend fun saveMusic(
            userId: String,
            musicDto: MusicDto,
        ): Result<Unit> =
            try {
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

        suspend fun deleteMusic(
            userId: String,
            musicId: String,
        ): Result<Unit> =
            try {
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

        suspend fun deleteAllMusics(userId: String): Result<Unit> =
            try {
                val snapshot =
                    firestore
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

        suspend fun updateComment(
            userId: String,
            musicId: String,
            comment: String,
        ): Result<Unit> =
            try {
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

        suspend fun saveUserProfile(
            userId: String,
            profileDto: UserProfileDto,
        ): Result<Unit> =
            try {
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

        suspend fun getUserProfile(userId: String): Result<UserProfileDto?> =
            try {
                val snapshot =
                    firestore
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

        suspend fun deleteUserAccount(userId: String): Result<Unit> =
            try {
                // 1. musics subcollection 모든 데이터 삭제
                deleteAllMusics(userId)
                // 2. signature_songs subcollection 모든 데이터 삭제
                deleteAllSignatureSongs(userId)
                // 3. profile subcollection 삭제
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(DOCUMENT_PROFILE)
                    .document(DOCUMENT_PROFILE)
                    .delete()
                    .await()
                // 4. user document 삭제
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .delete()
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }

        // 인스타그램 공유 설정 저장
        suspend fun saveInstagramShareSetting(
            userId: String,
            settingDto: InstagramShareSettingDto,
        ): Result<Unit> =
            try {
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_SETTINGS)
                    .document(DOCUMENT_INSTAGRAM_SHARE)
                    .set(settingDto, SetOptions.merge())
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }

        // 인스타그램 공유 설정 불러오기
        suspend fun getInstagramShareSetting(userId: String): Result<InstagramShareSettingDto?> =
            try {
                val snapshot =
                    firestore
                        .collection(COLLECTION_USERS)
                        .document(userId)
                        .collection(COLLECTION_SETTINGS)
                        .document(DOCUMENT_INSTAGRAM_SHARE)
                        .get()
                        .await()

                val setting = snapshot.toObject(InstagramShareSettingDto::class.java)
                Result.success(setting)
            } catch (e: Exception) {
                Result.failure(e)
            }

        // 인생곡 저장
        suspend fun saveSignatureSong(
            userId: String,
            lifeSong: SignatureSong,
        ): Result<Unit> =
            try {
                val lifeSongId = lifeSong.selectedAt.time.toString()
                val remoteSignatureSong = lifeSong.toRemoteDto().toFirestoreMap()

                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_SIGNATURE_SONGS)
                    .document(lifeSongId)
                    .set(remoteSignatureSong, SetOptions.merge())
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }

        // 모든 인생곡 불러오기
        suspend fun getAllSignatureSongs(userId: String): Result<List<SignatureSong>> =
            try {
                val snapshot =
                    firestore
                        .collection(COLLECTION_USERS)
                        .document(userId)
                        .collection(COLLECTION_SIGNATURE_SONGS)
                        .get()
                        .await()

                val lifeSongs =
                    snapshot.documents.mapNotNull { document ->
                        SignatureSongRemoteDto.fromDocument(document)?.toEntity()
                    }

                Result.success(lifeSongs)
            } catch (e: Exception) {
                Result.failure(e)
            }

        // 모든 인생곡 삭제
        suspend fun deleteAllSignatureSongs(userId: String): Result<Unit> =
            try {
                val snapshot =
                    firestore
                        .collection(COLLECTION_USERS)
                        .document(userId)
                        .collection(COLLECTION_SIGNATURE_SONGS)
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
