package com.theveloper.pixelplay.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration 41 → 42
 *
 * Adds an index on `songs.file_path`.
 *
 * `getSongByPath()` in MusicDao performs a `WHERE file_path = :path LIMIT 1` query
 * that was doing a full table scan on every call. With 10 000+ songs on a
 * mid-range device this can take 10–50 ms per lookup, and it is called during
 * every sync pass and URI resolution.
 *
 * Room already declares the index in SongEntity (index added in schema v42),
 * but existing installs at v41 need this manual migration to create it.
 */
val MIGRATION_41_42 = object : Migration(41, 42) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_songs_file_path` ON `songs` (`file_path`)"
        )
    }
}
