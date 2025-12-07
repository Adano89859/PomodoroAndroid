package com.example.pomodoro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pomodoro.ui.settings.SettingsScreen
import com.example.pomodoro.ui.tasks.TasksScreen
import com.example.pomodoro.ui.timer.PomodoroViewModel
import com.example.pomodoro.ui.timer.TimerScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: PomodoroViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Timer.route
    ) {
        composable(Screen.Timer.route) {
            TimerScreen(
                viewModel = viewModel,
                onNavigateToTasks = {
                    navController.navigate(Screen.Tasks.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Tasks.route) {
            TasksScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}