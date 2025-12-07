package com.example.pomodoro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class PomodoroTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val pomodorosCompleted: Int = 0,
    val timeSpentInSeconds: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val progressNotes: String = ""
)