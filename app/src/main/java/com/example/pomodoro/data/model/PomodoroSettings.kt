package com.tuapp.pomodoro.data.model

data class PomodoroSettings(
    val workDuration: Int = 25, // minutos
    val shortBreakDuration: Int = 5,
    val longBreakDuration: Int = 15,
    val pomodorosUntilLongBreak: Int = 4,
    val autoStartBreaks: Boolean = false,
    val autoStartPomodoros: Boolean = false,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)