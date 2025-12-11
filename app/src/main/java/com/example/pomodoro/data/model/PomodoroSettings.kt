package com.example.pomodoro.data.model

data class PomodoroSettings(
    val workDuration: Int = 25,
    val shortBreakDuration: Int = 5,
    val longBreakDuration: Int = 15,
    val pomodorosUntilLongBreak: Int = 4,
    val autoStartBreaks: Boolean = false,
    val autoStartPomodoros: Boolean = false,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val workMusicTrackId: Int = 1,
    val shortBreakMusicTrackId: Int = 11,
    val longBreakMusicTrackId: Int = 21,
    val appTheme: AppTheme = AppTheme.FRESITA_LIGHT  // ← CAMBIADO
)