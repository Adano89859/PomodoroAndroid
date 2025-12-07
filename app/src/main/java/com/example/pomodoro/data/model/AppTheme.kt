package com.example.pomodoro.data.model

enum class AppTheme(val displayName: String, val emoji: String) {
    FRESITA_LIGHT("Fresita", "🍓"),
    FRESITA_DARK("Fresita Oscuro", "🍓🌙"),
    OCEAN_LIGHT("Oceánico", "🌊"),
    OCEAN_DARK("Oceánico Oscuro", "🌊🌙"),
    FOREST_LIGHT("Bosque", "🌲"),
    FOREST_DARK("Bosque Oscuro", "🌲🌙"),
    SUNSET_LIGHT("Atardecer", "🌅"),
    SUNSET_DARK("Atardecer Oscuro", "🌅🌙"),
    PURPLE_LIGHT("Morado", "💜"),
    PURPLE_DARK("Morado Oscuro", "💜🌙"),
    SYSTEM("Sistema", "🔄"); // Sigue el tema del sistema
}