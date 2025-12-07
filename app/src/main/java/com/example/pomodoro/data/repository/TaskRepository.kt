package com.example.pomodoro.data.repository

import com.example.pomodoro.data.database.TaskDao
import com.example.pomodoro.data.model.PomodoroTask
import com.example.pomodoro.data.model.ProgressNote
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlin.collections.emptyList
import kotlinx.serialization.encodeToString

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

    suspend fun addTimeToTask(task: PomodoroTask, secondsToAdd: Int) {
        taskDao.updateTask(
            task.copy(timeSpentInSeconds = task.timeSpentInSeconds + secondsToAdd)
        )
    }

    suspend fun deleteAllCompletedTasks() {
        taskDao.deleteAllCompletedTasks()
    }

    suspend fun addProgressNote(task: PomodoroTask, note: String, sessionDuration: Int) {
        val progressNote = ProgressNote(
            text = note,
            timestamp = System.currentTimeMillis(),
            sessionDuration = sessionDuration
        )

        // Deserializar notas existentes
        val existingNotes = com.example.pomodoro.utils.ProgressNoteHelper.parseNotes(task.progressNotes)

        // Agregar nueva nota
        val updatedNotes = existingNotes + progressNote

        // Serializar de vuelta a JSON
        val notesJson = kotlinx.serialization.json.Json.encodeToString(updatedNotes)

        // Actualizar tarea
        taskDao.updateTask(task.copy(progressNotes = notesJson))
    }
}