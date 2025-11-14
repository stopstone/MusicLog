package com.stopstone.musicplaylist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.stopstone.musicplaylist.data.model.entity.SignatureSong
import kotlinx.coroutines.flow.Flow

@Dao
interface SignatureSongDao {
    @Insert
    suspend fun insert(signatureSong: SignatureSong)

    @Query("SELECT * FROM signature_song WHERE isActive = 1 LIMIT 1")
    fun getActiveSignatureSong(): Flow<SignatureSong?>

    @Query("SELECT * FROM signature_song WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveSignatureSongOnce(): SignatureSong?

    @Query("SELECT * FROM signature_song ORDER BY selectedAt DESC")
    fun getAllSignatureSongs(): Flow<List<SignatureSong>>

    @Query("SELECT * FROM signature_song ORDER BY selectedAt DESC")
    suspend fun getAllSignatureSongsOnce(): List<SignatureSong>

    @Query("UPDATE signature_song SET isActive = 0 WHERE isActive = 1")
    suspend fun deactivateAllSignatureSongs()

    @Transaction
    suspend fun setNewSignatureSong(signatureSong: SignatureSong) {
        deactivateAllSignatureSongs()
        insert(signatureSong)
    }

    @Query("DELETE FROM signature_song")
    suspend fun deleteAllSignatureSongs()
}
