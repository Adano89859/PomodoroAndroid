package com.example.pomodoro.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pomodoro.data.model.PomodoroTask
import com.example.pomodoro.data.model.User
import com.example.pomodoro.data.model.UnlockedMusic
import com.example.pomodoro.utils.MusicCatalog
import com.example.pomodoro.data.model.DailyStats
import com.example.pomodoro.data.model.PurchasedRoomItem
import com.example.pomodoro.data.model.ImportedMusic

@Database(
    entities = [
        PomodoroTask::class,
        User::class,
        UnlockedMusic::class,
        DailyStats::class,
        PurchasedRoomItem::class,
        ImportedMusic::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun musicDao(): MusicDao
    abstract fun dailyStatsDao(): DailyStatsDao
    abstract fun roomItemDao(): RoomItemDao
    abstract fun importedMusicDao(): ImportedMusicDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks ADD COLUMN timeSpentInSeconds INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks ADD COLUMN progressNotes TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS user (
                        id INTEGER PRIMARY KEY NOT NULL,
                        coins INTEGER NOT NULL DEFAULT 0,
                        totalPomodoros INTEGER NOT NULL DEFAULT 0,
                        totalTasksCompleted INTEGER NOT NULL DEFAULT 0,
                        totalNotesWritten INTEGER NOT NULL DEFAULT 0
                    )
                """
                )
                database.execSQL("INSERT OR IGNORE INTO user (id, coins) VALUES (1, 0)")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS unlocked_music (
                        trackId INTEGER PRIMARY KEY NOT NULL
                    )
                """
                )

                MusicCatalog.freeTracks.forEach { trackId ->
                    database.execSQL("INSERT OR IGNORE INTO unlocked_music (trackId) VALUES ($trackId)")
                }
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS daily_stats (
                        date TEXT PRIMARY KEY NOT NULL,
                        pomodorosCompleted INTEGER NOT NULL DEFAULT 0,
                        tasksCompleted INTEGER NOT NULL DEFAULT 0,
                        notesWritten INTEGER NOT NULL DEFAULT 0,
                        timeWorkedInSeconds INTEGER NOT NULL DEFAULT 0
                    )
                """
                )
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS purchased_room_items (
                        itemId INTEGER PRIMARY KEY NOT NULL
                    )
                """)
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS imported_music (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        displayName TEXT NOT NULL,
                        originalFileName TEXT NOT NULL,
                        internalFilePath TEXT NOT NULL,
                        sessionType TEXT NOT NULL,
                        isPurchased INTEGER NOT NULL DEFAULT 0,
                        durationSeconds INTEGER NOT NULL DEFAULT 0
                    )
                """)
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pomodoro_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,  // ← AGREGADA
                        MIGRATION_7_8
                    )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}