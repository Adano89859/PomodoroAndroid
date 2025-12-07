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
    private val _timeRemaining = MutableStateFlow(25 * 60)
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

    // NUEVO: Variables para trackear tiempo trabajado
    private var sessionStartTime: Long = 0
    private var accumulatedWorkTime: Int = 0 // en segundos

    // NUEVO: Estados para diálogos
    private val _showProgressDialog = MutableStateFlow(false)
    val showProgressDialog: StateFlow<Boolean> = _showProgressDialog.asStateFlow()

    private val _showCelebrationDialog = MutableStateFlow(false)
    val showCelebrationDialog: StateFlow<Boolean> = _showCelebrationDialog.asStateFlow()

    private val _celebrationSessionType = MutableStateFlow<SessionType?>(null)
    val celebrationSessionType: StateFlow<SessionType?> = _celebrationSessionType.asStateFlow()

    // Bonus de tiempo acumulado
    private var bonusTimeInSeconds = 0

    init {
        resetTimer()
    }

    fun startTimer() {
        if (_timerState.value == TimerState.RUNNING) return

        _timerState.value = TimerState.RUNNING

        // NUEVO: Registrar inicio de sesión de trabajo
        if (_sessionType.value == SessionType.WORK) {
            sessionStartTime = System.currentTimeMillis()
        }

        // Iniciar música si está habilitada
        if (_settings.value.soundEnabled) {
            musicPlayer.playMusicForSession(
                _sessionType.value,
                true,
                _settings.value.workMusicTrackId,
                _settings.value.shortBreakMusicTrackId,
                _settings.value.longBreakMusicTrackId
            )
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

        // NUEVO: Guardar tiempo trabajado al pausar (solo en sesiones de trabajo)
        if (_sessionType.value == SessionType.WORK && sessionStartTime > 0) {
            val workTime = ((System.currentTimeMillis() - sessionStartTime) / 1000).toInt()
            accumulatedWorkTime += workTime
            sessionStartTime = 0
        }
    }

    fun skipSession() {
        timerJob?.cancel()

        // NUEVO: Guardar tiempo trabajado antes de saltar
        if (_sessionType.value == SessionType.WORK && sessionStartTime > 0) {
            val workTime = ((System.currentTimeMillis() - sessionStartTime) / 1000).toInt()
            accumulatedWorkTime += workTime
            saveAccumulatedTime()
        }

        _timerState.value = TimerState.IDLE
        onTimerComplete()
    }

    private fun onTimerComplete() {
        // Calcular y guardar tiempo trabajado si es sesión de trabajo
        if (_sessionType.value == SessionType.WORK && sessionStartTime > 0) {
            val workTime = ((System.currentTimeMillis() - sessionStartTime) / 1000).toInt()
            accumulatedWorkTime += workTime
            saveAccumulatedTime()
            sessionStartTime = 0

            // NUEVO: Mostrar diálogo de progreso si hay tarea activa
            if (_currentTask.value != null) {
                _showProgressDialog.value = true
                return // Pausar aquí hasta que se complete el diálogo
            }
        }

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

        // NUEVO: Mostrar diálogo de celebración
        _celebrationSessionType.value = _sessionType.value
        _showCelebrationDialog.value = true

        when (_sessionType.value) {
            SessionType.WORK -> {
                _completedPomodoros.value++

                _currentTask.value?.let { task ->
                    viewModelScope.launch {
                        repository.incrementPomodoro(task)
                    }
                }

                if (_completedPomodoros.value >= _settings.value.pomodorosUntilLongBreak) {
                    _sessionType.value = SessionType.LONG_BREAK
                    _completedPomodoros.value = 0
                } else {
                    _sessionType.value = SessionType.SHORT_BREAK
                }
            }
            SessionType.SHORT_BREAK, SessionType.LONG_BREAK -> {
                _sessionType.value = SessionType.WORK
                // Resetear bonus de tiempo
                bonusTimeInSeconds = 0
            }
        }

        resetTimer()

        if (shouldAutoStart()) {
            startTimer()
        }
    }

    // NUEVO: Guardar tiempo acumulado en la tarea
    private fun saveAccumulatedTime() {
        _currentTask.value?.let { task ->
            if (accumulatedWorkTime > 0) {
                viewModelScope.launch {
                    repository.addTimeToTask(task, accumulatedWorkTime)
                    // Recargar la tarea para mostrar el tiempo actualizado
                    _currentTask.value = task.copy(
                        timeSpentInSeconds = task.timeSpentInSeconds + accumulatedWorkTime
                    )
                    accumulatedWorkTime = 0
                }
            }
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
        // Guardar tiempo acumulado de la tarea anterior
        if (_currentTask.value != null && accumulatedWorkTime > 0) {
            saveAccumulatedTime()
        }

        _currentTask.value = task
        accumulatedWorkTime = 0
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

    // NUEVO: Formatear tiempo trabajado (puede ser más de 60 minutos)
    fun formatWorkTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return if (hours > 0) {
            "${hours}h ${minutes}m"
        } else {
            "${minutes}m"
        }
    }

    // NUEVO: Guardar nota de progreso
    fun saveProgressNote(noteText: String) {
        _showProgressDialog.value = false

        _currentTask.value?.let { task ->
            if (noteText.isNotBlank()) {
                // Calcular bonus de tiempo
                val bonus = calculateBonusTime(noteText.length)
                bonusTimeInSeconds = bonus

                viewModelScope.launch {
                    repository.addProgressNote(task, noteText, accumulatedWorkTime)
                }
            }
        }

        // Continuar con el flujo normal
        continueAfterProgressNote()
    }

    private fun calculateBonusTime(textLength: Int): Int {
        return when {
            textLength < 20 -> 0
            textLength < 50 -> 30
            textLength < 100 -> 60
            textLength < 200 -> 90
            else -> 120
        }
    }

    private fun continueAfterProgressNote() {
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

        // Mostrar celebración
        _celebrationSessionType.value = _sessionType.value
        _showCelebrationDialog.value = true

        // Cambiar de sesión
        _completedPomodoros.value++

        _currentTask.value?.let { task ->
            viewModelScope.launch {
                repository.incrementPomodoro(task)
            }
        }

        if (_completedPomodoros.value >= _settings.value.pomodorosUntilLongBreak) {
            _sessionType.value = SessionType.LONG_BREAK
            _completedPomodoros.value = 0
        } else {
            _sessionType.value = SessionType.SHORT_BREAK
        }

        resetTimer()

        if (shouldAutoStart()) {
            startTimer()
        }
    }

    fun dismissCelebration() {
        _showCelebrationDialog.value = false
        _celebrationSessionType.value = null
    }

    // NUEVO: Override de resetTimer para aplicar bonus
    fun resetTimer() {
        _timerState.value = TimerState.IDLE
        timerJob?.cancel()
        sessionStartTime = 0

        val baseDuration = when (_sessionType.value) {
            SessionType.WORK -> _settings.value.workDuration * 60
            SessionType.SHORT_BREAK -> _settings.value.shortBreakDuration * 60
            SessionType.LONG_BREAK -> _settings.value.longBreakDuration * 60
        }

        // Aplicar bonus si es descanso
        val finalDuration = if (_sessionType.value != SessionType.WORK) {
            baseDuration + bonusTimeInSeconds
        } else {
            baseDuration
        }

        _timeRemaining.value = finalDuration
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        musicPlayer.stop()

        // Guardar tiempo acumulado antes de destruir el ViewModel
        if (accumulatedWorkTime > 0) {
            saveAccumulatedTime()
        }
    }
}