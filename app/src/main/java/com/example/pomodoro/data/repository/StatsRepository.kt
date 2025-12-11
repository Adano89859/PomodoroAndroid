package com.example.pomodoro.data.repository

import com.example.pomodoro.data.database.DailyStatsDao
import com.example.pomodoro.data.model.DailyStats
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class StatsRepository(private val dailyStatsDao: DailyStatsDao) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getLast7Days(): Flow<List<DailyStats>> = dailyStatsDao.getLast7Days()

    fun getLast30Days(): Flow<List<DailyStats>> = dailyStatsDao.getLast30Days()

    suspend fun incrementPomodoro() {
        val today = getTodayDate()
        val stats = dailyStatsDao.getStatsForDate(today) ?: DailyStats(date = today)
        dailyStatsDao.insertOrUpdate(stats.copy(pomodorosCompleted = stats.pomodorosCompleted + 1))
    }

    suspend fun incrementTask() {
        val today = getTodayDate()
        val stats = dailyStatsDao.getStatsForDate(today) ?: DailyStats(date = today)
        dailyStatsDao.insertOrUpdate(stats.copy(tasksCompleted = stats.tasksCompleted + 1))
    }

    suspend fun incrementNote() {
        val today = getTodayDate()
        val stats = dailyStatsDao.getStatsForDate(today) ?: DailyStats(date = today)
        dailyStatsDao.insertOrUpdate(stats.copy(notesWritten = stats.notesWritten + 1))
    }

    suspend fun addTimeWorked(seconds: Int) {
        val today = getTodayDate()
        val stats = dailyStatsDao.getStatsForDate(today) ?: DailyStats(date = today)
        dailyStatsDao.insertOrUpdate(stats.copy(timeWorkedInSeconds = stats.timeWorkedInSeconds + seconds))
    }

    suspend fun getTotalPomodoros(): Int = dailyStatsDao.getTotalPomodoros() ?: 0

    suspend fun getTotalTasks(): Int = dailyStatsDao.getTotalTasks() ?: 0

    suspend fun getTotalNotes(): Int = dailyStatsDao.getTotalNotes() ?: 0

    suspend fun getTotalTimeWorked(): Int = dailyStatsDao.getTotalTimeWorked() ?: 0

    suspend fun getCurrentStreak(): Int {
        val allDays = dailyStatsDao.getAllDaysWithActivity()
        if (allDays.isEmpty()) return 0

        var streak = 0
        val calendar = Calendar.getInstance()

        for (day in allDays) {
            val dayDate = dateFormat.parse(day.date) ?: break
            val expectedDate = dateFormat.format(calendar.time)

            if (day.date == expectedDate) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        return streak
    }

    private fun getTodayDate(): String {
        return dateFormat.format(Date())
    }
}