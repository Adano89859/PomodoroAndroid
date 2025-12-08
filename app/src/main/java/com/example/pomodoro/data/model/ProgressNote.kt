package com.example.pomodoro.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Required

@Serializable
data class ProgressNote(
    val text: String,
    @Required // ← ESTO obliga a serializar el campo siempre
    val timestamp: Long = System.currentTimeMillis(),
    val sessionDuration: Int
)