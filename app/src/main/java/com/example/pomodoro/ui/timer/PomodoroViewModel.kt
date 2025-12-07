package com.example.pomodoro.ui.timer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.database.AppDatabase
import com.example.pomodoro.data.model.PomodoroSettings
import com.example.pomodoro.data.model.PomodoroTask
import com.example.pomodoro.data.model.SessionType
import com.example.pomodoro.data.model.TimerState
import com.example.pomodoro.data.repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.pomodoro.utils.NotificationHelper
import com.example.pomodoro.utils.MusicPlayer

class PomodoroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    private val notificationHelper = NotificationHelper(application)
    private val musicPlayer = MusicPlayer(application)

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
    }

    // Settings
    private val _settings = MutableStateFlow(PomodoroSettings())
    val settings: StateFlow<PomodoroSettings> = _settings.asStateFlow()

    // Timer State
    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    // Session Type
    private val _sessionType = MutableStateFlow(SessionType.WORK)
    val sessionType: StateFlow<SessionType> = _sessionType.asStateFlow()

    // Time remaining in seconds
    private val _timeRemaining = MutableStateFlow(25 * 60) // 25 minutos por defecto
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    // Completed pomodoros in current cycle
    private val _completedPomodoros = MutableStateFlow(0)
    val completedPomodoros: StateFlow<Int> = _completedPomodoros.asStateFlow()

    // Current task
    private val _currentTask = MutableStateFlow<PomodoroTask?>(null)
    val currentTask: StateFlow<PomodoroTask?> = _currentTask.asStateFlow()

    // Tasks from repository
    val activeTasks = repository.activeTasks
    val completedTasks = repository.completedTasks

    private var timerJob: Job? = null

    init {
        resetTimer()
    }

    fun startTimer() {
        if (_timerState.value == TimerState.RUNNING) return

        _timerState.value = TimerState.RUNNING

        // Iniciar música si está habilitada
        if (_settings.value.soundEnabled) {
            musicPlayer.playMusicForSession(_sessionType.value, true)
        }

        timerJob = viewModelScope.launch {
            while (_timeRemaining.value > 0 && _timerState.value == TimerState.RUNNING) {
                delay(1000)
                _timeRemaining.value--
            }

            if (_timeRemaining.value == 0) {
                onTimerComplete()
            }
        }
    }

    fun pauseTimer() {
        _timerState.value = TimerState.PAUSED
        timerJob?.cancel()
        musicPlayer.pause()
    }

    fun resetTimer() {
        _timerState.value = TimerState.IDLE
        timerJob?.cancel()

        _timeRemaining.value = when (_sessionType.value) {
            SessionType.WORK -> _settings.value.workDuration * 60
            SessionType.SHORT_BREAK -> _settings.value.shortBreakDuration * 60
            SessionType.LONG_BREAK -> _settings.value.longBreakDuration * 60
        }
    }

    fun skipSession() {
        timerJob?.cancel()
        _timerState.value = TimerState.IDLE
        onTimerComplete()
    }

    private fun onTimerComplete() {
        // Mostrar notificación
        notificationHelper.showSessionCompleteNotification(_sessionType.value)

        // Vibrar si está habilitado
        if (_settings.value.vibrationEnabled) {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getApplication<Application>().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        }

        when (_sessionType.value) {
            SessionType.WORK -> {
                // Incrementar pomodoros completados
                _completedPomodoros.value++

                // Incrementar pomodoro de la tarea actual
                _currentTask.value?.let { task ->
                    viewModelScope.launch {
                        repository.incrementPomodoro(task)
                    }
                }

                // Decidir siguiente sesión
                if (_completedPomodoros.value >= _settings.value.pomodorosUntilLongBreak) {
                    _sessionType.value = SessionType.LONG_BREAK
                    _completedPomodoros.value = 0
                } else {
                    _sessionType.value = SessionType.SHORT_BREAK
                }
            }
            SessionType.SHORT_BREAK, SessionType.LONG_BREAK -> {
                _sessionType.value = SessionType.WORK
            }
        }

        resetTimer()

        // Auto-start si está configurado
        if (shouldAutoStart()) {
            startTimer()
        }
    }

    private fun shouldAutoStart(): Boolean {
        return when (_sessionType.value) {
            SessionType.WORK -> _settings.value.autoStartPomodoros
            else -> _settings.value.autoStartBreaks
        }
    }

    fun updateSettings(newSettings: PomodoroSettings) {
        _settings.value = newSettings
        if (_timerState.value == TimerState.IDLE) {
            resetTimer()
        }
    }

    fun setCurrentTask(task: PomodoroTask?) {
        _currentTask.value = task
    }

    // Task management functions
    fun addTask(title: String, description: String = "") {
        viewModelScope.launch {
            repository.insertTask(
                PomodoroTask(
                    title = title,
                    description = description
                )
            )
        }
    }

    fun updateTask(task: PomodoroTask) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun completeTask(task: PomodoroTask) {
        viewModelScope.launch {
            repository.completeTask(task)
            if (_currentTask.value?.id == task.id) {
                _currentTask.value = null
            }
        }
    }

    fun deleteTask(task: PomodoroTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
            if (_currentTask.value?.id == task.id) {
                _currentTask.value = null
            }
        }
    }

    fun deleteAllCompletedTasks() {
        viewModelScope.launch {
            repository.deleteAllCompletedTasks()
        }
    }

    // Formato de tiempo
    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        musicPlayer.stop()
    }
}