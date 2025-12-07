package com.example.pomodoro.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProgressNote(
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionDuration: Int // duración de la sesión en segundos
)