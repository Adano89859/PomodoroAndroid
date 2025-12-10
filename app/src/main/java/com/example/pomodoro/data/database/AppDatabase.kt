package com.example.pomodoro.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pomodoro.data.model.PomodoroTask

@Database(entities = [PomodoroTask::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migración de versión 1 a 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks ADD COLUMN timeSpentInSeconds INTEGER NOT NULL DEFAULT 0")
            }
        }

        // NUEVA Migración de versión 2 a 3
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
                // Insertar usuario inicial
                database.execSQL("INSERT OR IGNORE INTO user (id, coins) VALUES (1, 0)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pomodoro_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}