package com.example.pomodoro.data.database

import androidx.room.*
import com.example.pomodoro.data.model.DailyStats
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatsDao {

    @Query("SELECT * FROM daily_stats WHERE date = :date LIMIT 1")
    suspend fun getStatsForDate(date: String): DailyStats?

    @Query("SELECT * FROM daily_stats ORDER BY date DESC LIMIT 7")
    fun getLast7Days(): Flow<List<DailyStats>>

    @Query("SELECT * FROM daily_stats ORDER BY date DESC LIMIT 30")
    fun getLast30Days(): Flow<List<DailyStats>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: DailyStats)

    @Query("SELECT SUM(pomodorosCompleted) FROM daily_stats")
    suspend fun getTotalPomodoros(): Int?

    @Query("SELECT SUM(tasksCompleted) FROM daily_stats")
    suspend fun getTotalTasks(): Int?

    @Query("SELECT SUM(notesWritten) FROM daily_stats")
    suspend fun getTotalNotes(): Int?

    @Query("SELECT SUM(timeWorkedInSeconds) FROM daily_stats")
    suspend fun getTotalTimeWorked(): Int?

    // Racha de días consecutivos
    @Query("SELECT * FROM daily_stats WHERE pomodorosCompleted > 0 ORDER BY date DESC")
    suspend fun getAllDaysWithActivity(): List<DailyStats>
}