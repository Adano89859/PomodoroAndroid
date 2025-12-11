package com.example.pomodoro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_stats")
data class DailyStats(
    @PrimaryKey val date: String, // Formato: "2024-12-11"
    val pomodorosCompleted: Int = 0,
    val tasksCompleted: Int = 0,
    val notesWritten: Int = 0,
    val timeWorkedInSeconds: Int = 0
)