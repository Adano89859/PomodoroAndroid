package com.example.pomodoro.utils

import com.example.pomodoro.data.model.ProgressNote
import kotlinx.serialization.json.Json

object ProgressNoteHelper {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun parseNotes(notesJson: String): List<ProgressNote> {
        if (notesJson.isEmpty()) return emptyList()

        return try {
            json.decodeFromString<List<ProgressNote>>(notesJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "hace ${days}d"
            hours > 0 -> "hace ${hours}h"
            minutes > 0 -> "hace ${minutes}m"
            else -> "hace un momento"
        }
    }
}