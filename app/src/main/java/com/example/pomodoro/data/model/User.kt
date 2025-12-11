package com.example.pomodoro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int = 1, // Solo habrá un usuario
    val coins: Int = 0,
    val totalPomodoros: Int = 0,
    val totalTasksCompleted: Int = 0,
    val totalNotesWritten: Int = 0
)