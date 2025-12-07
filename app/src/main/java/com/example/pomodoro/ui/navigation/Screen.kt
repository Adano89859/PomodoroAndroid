package com.example.pomodoro.ui.navigation

sealed class Screen(val route: String) {
    object Timer : Screen("timer")
    object Tasks : Screen("tasks")
    object Settings : Screen("settings")
}