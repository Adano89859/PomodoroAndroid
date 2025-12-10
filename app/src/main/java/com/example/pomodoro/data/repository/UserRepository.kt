package com.example.pomodoro.data.repository

import com.example.pomodoro.data.database.UserDao
import com.example.pomodoro.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    val user: Flow<User?> = userDao.getUser()

    suspend fun ensureUserExists() {
        val existingUser = userDao.getUserOnce()
        if (existingUser == null) {
            userDao.insertUser(User(id = 1, coins = 0))
        }
    }

    suspend fun addCoins(amount: Int, reason: CoinReward) {
        userDao.addCoins(amount)

        // Actualizar estadísticas según el motivo
        val user = userDao.getUserOnce() ?: return
        when (reason) {
            CoinReward.POMODORO_COMPLETED -> {
                userDao.updateUser(user.copy(totalPomodoros = user.totalPomodoros + 1))
            }
            CoinReward.TASK_COMPLETED -> {
                userDao.updateUser(user.copy(totalTasksCompleted = user.totalTasksCompleted + 1))
            }
            CoinReward.PROGRESS_NOTE -> {
                userDao.updateUser(user.copy(totalNotesWritten = user.totalNotesWritten + 1))
            }
            else -> { /* Otros casos */ }
        }
    }

    suspend fun subtractCoins(amount: Int): Boolean {
        val user = userDao.getUserOnce() ?: return false
        return if (user.coins >= amount) {
            userDao.subtractCoins(amount)
            true
        } else {
            false
        }
    }
}

// Enum para rastrear por qué se otorgan monedas
enum class CoinReward(val amount: Int, val displayName: String) {
    POMODORO_COMPLETED(5, "Pomodoro completado"),
    TASK_COMPLETED(20, "Tarea completada"),
    PROGRESS_NOTE(10, "Nota de progreso"),
    TIME_DEDICATED(2, "Tiempo dedicado"), // Por cada minuto
}