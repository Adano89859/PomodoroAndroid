package com.example.pomodoro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pomodoro.ui.settings.SettingsScreen
import com.example.pomodoro.ui.tasks.TasksScreen
import com.example.pomodoro.ui.timer.PomodoroViewModel
import com.example.pomodoro.ui.timer.TimerScreen
import com.example.pomodoro.ui.shop.ShopScreen  // ← NUEVO

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
                },
                onNavigateToShop = {  // ← NUEVO
                    navController.navigate(Screen.Shop.route)
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

        // ← NUEVO: Ruta de la tienda
        composable(Screen.Shop.route) {
            ShopScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}