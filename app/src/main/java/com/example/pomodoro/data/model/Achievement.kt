package com.example.pomodoro.data.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val requirement: Int,
    val category: AchievementCategory
)

enum class AchievementCategory {
    POMODOROS,
    TASKS,
    NOTES,
    STREAK,
    MUSIC,
    ROOMS
}

object AchievementCatalog {
    val achievements = listOf(
        // Pomodoros
        Achievement(
            id = "first_pomodoro",
            title = "Primer Paso",
            description = "Completa tu primer pomodoro",
            emoji = "🍅",
            requirement = 1,
            category = AchievementCategory.POMODOROS
        ),
        Achievement(
            id = "pomodoro_10",
            title = "Enfocado",
            description = "Completa 10 pomodoros",
            emoji = "🔟",
            requirement = 10,
            category = AchievementCategory.POMODOROS
        ),
        Achievement(
            id = "pomodoro_50",
            title = "Productivo",
            description = "Completa 50 pomodoros",
            emoji = "⭐",
            requirement = 50,
            category = AchievementCategory.POMODOROS
        ),
        Achievement(
            id = "pomodoro_100",
            title = "Maestro",
            description = "Completa 100 pomodoros",
            emoji = "👑",
            requirement = 100,
            category = AchievementCategory.POMODOROS
        ),
        Achievement(
            id = "pomodoro_500",
            title = "Leyenda",
            description = "Completa 500 pomodoros",
            emoji = "🏆",
            requirement = 500,
            category = AchievementCategory.POMODOROS
        ),

        // Tareas
        Achievement(
            id = "task_1",
            title = "Organizador",
            description = "Completa tu primera tarea",
            emoji = "✅",
            requirement = 1,
            category = AchievementCategory.TASKS
        ),
        Achievement(
            id = "task_25",
            title = "Eficiente",
            description = "Completa 25 tareas",
            emoji = "📋",
            requirement = 25,
            category = AchievementCategory.TASKS
        ),
        Achievement(
            id = "task_100",
            title = "Imparable",
            description = "Completa 100 tareas",
            emoji = "🚀",
            requirement = 100,
            category = AchievementCategory.TASKS
        ),

        // Notas
        Achievement(
            id = "note_1",
            title = "Escritor",
            description = "Escribe tu primera nota",
            emoji = "📝",
            requirement = 1,
            category = AchievementCategory.NOTES
        ),
        Achievement(
            id = "note_20",
            title = "Diario",
            description = "Escribe 20 notas",
            emoji = "📖",
            requirement = 20,
            category = AchievementCategory.NOTES
        ),
        Achievement(
            id = "note_100",
            title = "Autor",
            description = "Escribe 100 notas",
            emoji = "✍️",
            requirement = 100,
            category = AchievementCategory.NOTES
        ),

        // Racha
        Achievement(
            id = "streak_3",
            title = "Constante",
            description = "3 días de racha",
            emoji = "🔥",
            requirement = 3,
            category = AchievementCategory.STREAK
        ),
        Achievement(
            id = "streak_7",
            title = "Dedicado",
            description = "7 días de racha",
            emoji = "💪",
            requirement = 7,
            category = AchievementCategory.STREAK
        ),
        Achievement(
            id = "streak_30",
            title = "Disciplinado",
            description = "30 días de racha",
            emoji = "🌟",
            requirement = 30,
            category = AchievementCategory.STREAK
        ),

        // Música
        Achievement(
            id = "music_5",
            title = "Melómano",
            description = "Desbloquea 5 canciones",
            emoji = "🎵",
            requirement = 5,
            category = AchievementCategory.MUSIC
        ),
        Achievement(
            id = "music_15",
            title = "Coleccionista",
            description = "Desbloquea 15 canciones",
            emoji = "🎶",
            requirement = 15,
            category = AchievementCategory.MUSIC
        ),
        Achievement(
            id = "music_all",
            title = "DJ Master",
            description = "Desbloquea todas las canciones",
            emoji = "🎧",
            requirement = 30,
            category = AchievementCategory.MUSIC
        ),

        // Habitaciones
        Achievement(
            id = "room_1",
            title = "Decorador",
            description = "Completa tu primera habitación",
            emoji = "🏡",
            requirement = 1,
            category = AchievementCategory.ROOMS
        ),
        Achievement(
            id = "room_all",
            title = "Arquitecto",
            description = "Completa todas las habitaciones",
            emoji = "🏛️",
            requirement = 3,
            category = AchievementCategory.ROOMS
        )
    )

    fun getAchievementById(id: String): Achievement? = achievements.find { it.id == id }
}