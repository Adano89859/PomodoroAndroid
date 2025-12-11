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

@Database(
    entities = [PomodoroTask::class, User::class, UnlockedMusic::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun musicDao(): MusicDao

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
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user (
                        id INTEGER PRIMARY KEY NOT NULL,
                        coins INTEGER NOT NULL DEFAULT 0,
                        totalPomodoros INTEGER NOT NULL DEFAULT 0,
                        totalTasksCompleted INTEGER NOT NULL DEFAULT 0,
                        totalNotesWritten INTEGER NOT NULL DEFAULT 0
                    )
                """)
                database.execSQL("INSERT OR IGNORE INTO user (id, coins) VALUES (1, 0)")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crear tabla de música desbloqueada
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS unlocked_music (
                        trackId INTEGER PRIMARY KEY NOT NULL
                    )
                """)

                // Desbloquear canciones gratuitas por defecto
                MusicCatalog.freeTracks.forEach { trackId ->
                    database.execSQL("INSERT OR IGNORE INTO unlocked_music (trackId) VALUES ($trackId)")
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pomodoro_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}