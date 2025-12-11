package com.example.pomodoro.data.model

data class RoomItem(
    val id: Int,
    val name: String,
    val emoji: String,
    val description: String,
    val price: Int,
    val roomType: RoomType
)

enum class RoomType(val displayName: String, val emoji: String) {
    GARDEN("Jardín", "🌳"),
    OFFICE("Escritorio", "💼"),
    BEDROOM("Dormitorio", "🛏️")
}