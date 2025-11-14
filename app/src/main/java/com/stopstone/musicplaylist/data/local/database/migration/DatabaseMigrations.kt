package com.stopstone.musicplaylist.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val migration1To2: Migration =
        object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                addEmotionsColumn(database)
                addCommentColumn(database)
                createSignatureSongTable(database)
            }

            private fun addEmotionsColumn(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    ALTER TABLE `daily_tracks`
                    ADD COLUMN `emotions` TEXT NOT NULL DEFAULT '[]'
                    """.trimIndent(),
                )
            }

            private fun addCommentColumn(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    ALTER TABLE `daily_tracks`
                    ADD COLUMN `comment` TEXT
                    """.trimIndent(),
                )
            }

            private fun createSignatureSongTable(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `signature_song` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `track` TEXT NOT NULL,
                        `selectedAt` INTEGER NOT NULL,
                        `isActive` INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
            }
        }
}

