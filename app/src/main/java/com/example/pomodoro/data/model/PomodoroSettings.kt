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
    val workMusicTrackId: String = "work_focus_1",
    val shortBreakMusicTrackId: String = "break_short_chill_1",
    val longBreakMusicTrackId: String = "break_long_deep_1",
    val appTheme: AppTheme = AppTheme.FRESITA_LIGHT
)