package com.stopstone.musicplaylist.domain.repository.my

import com.stopstone.musicplaylist.data.model.entity.SignatureSong
import kotlinx.coroutines.flow.Flow

interface SignatureSongRepository {
    suspend fun setSignatureSong(signatureSong: SignatureSong)

    fun getActiveSignatureSong(): Flow<SignatureSong?>

    suspend fun getActiveSignatureSongOnce(): SignatureSong?

    fun getAllSignatureSongs(): Flow<List<SignatureSong>>

    suspend fun getAllSignatureSongsOnce(): List<SignatureSong>

    suspend fun deleteAllSignatureSongs()

    suspend fun syncFromFirestore(userId: String): Result<Unit>
}
