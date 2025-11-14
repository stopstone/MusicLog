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
        }

    val migration2To3: Migration =
        object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                addEmotionsColumn(database)
                addCommentColumn(database)
                createSignatureSongTable(database)
            }
        }

    private fun addEmotionsColumn(database: SupportSQLiteDatabase) {
        addColumnIfMissing(
            database = database,
            tableName = "daily_tracks",
            columnName = "emotions",
            columnDefinition = "TEXT NOT NULL DEFAULT '[]'",
        )
    }

    private fun addCommentColumn(database: SupportSQLiteDatabase) {
        addColumnIfMissing(
            database = database,
            tableName = "daily_tracks",
            columnName = "comment",
            columnDefinition = "TEXT",
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

    private fun addColumnIfMissing(
        database: SupportSQLiteDatabase,
        tableName: String,
        columnName: String,
        columnDefinition: String,
    ) {
        if (database.hasColumn(tableName, columnName)) {
            return
        }
        database.execSQL(
            """
            ALTER TABLE `$tableName`
            ADD COLUMN `$columnName` $columnDefinition
            """.trimIndent(),
        )
    }

    private fun SupportSQLiteDatabase.hasColumn(
        tableName: String,
        columnName: String,
    ): Boolean {
        query("PRAGMA table_info(`$tableName`)").use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            while (cursor.moveToNext()) {
                if (cursor.getString(nameIndex) == columnName) {
                    return true
                }
            }
        }
        return false
    }
}

