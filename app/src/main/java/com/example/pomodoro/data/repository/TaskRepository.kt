package com.example.pomodoro.data.repository

import com.example.pomodoro.data.database.TaskDao
import com.example.pomodoro.data.model.PomodoroTask
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val activeTasks: Flow<List<PomodoroTask>> = taskDao.getActiveTasks()
    val completedTasks: Flow<List<PomodoroTask>> = taskDao.getCompletedTasks()

    suspend fun insertTask(task: PomodoroTask) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: PomodoroTask) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: PomodoroTask) {
        taskDao.deleteTask(task)
    }

    suspend fun completeTask(task: PomodoroTask) {
        taskDao.updateTask(
            task.copy(
                isCompleted = true,
                completedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun incrementPomodoro(task: PomodoroTask) {
        taskDao.updateTask(
            task.copy(pomodorosCompleted = task.pomodorosCompleted + 1)
        )
    }

    suspend fun deleteAllCompletedTasks() {
        taskDao.deleteAllCompletedTasks()
    }
}