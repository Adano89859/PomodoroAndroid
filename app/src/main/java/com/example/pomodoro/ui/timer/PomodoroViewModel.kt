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
import com.example.pomodoro.data.repository.UserRepository
import com.example.pomodoro.data.repository.StatsRepository  // ← NUEVO
import com.example.pomodoro.data.repository.CoinReward
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
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
    private val userRepository: UserRepository
    private val statsRepository: StatsRepository  // ← NUEVO
    private val notificationHelper = NotificationHelper(application)
    private val musicPlayer = MusicPlayer(application)

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        val userDao = AppDatabase.getDatabase(application).userDao()
        val dailyStatsDao = AppDatabase.getDatabase(application).dailyStatsDao()  // ← NUEVO
        repository = TaskRepository(taskDao)
        userRepository = UserRepository(userDao)
        statsRepository = StatsRepository(dailyStatsDao)  // ← NUEVO

        viewModelScope.launch {
            userRepository.ensureUserExists()
        }
    }

    val userCoins: StateFlow<Int> = userRepository.user
        .map { it?.coins ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _settings = MutableStateFlow(PomodoroSettings())
    val settings: StateFlow<PomodoroSettings> = _settings.asStateFlow()

    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _sessionType = MutableStateFlow(SessionType.WORK)
    val sessionType: StateFlow<SessionType> = _sessionType.asStateFlow()

    private val _timeRemaining = MutableStateFlow(25 * 60)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private val _completedPomodoros = MutableStateFlow(0)
    val completedPomodoros: StateFlow<Int> = _completedPomodoros.asStateFlow()

    private val _currentTask = MutableStateFlow<PomodoroTask?>(null)
    val currentTask: StateFlow<PomodoroTask?> = _currentTask.asStateFlow()

    val activeTasks = repository.activeTasks
    val completedTasks = repository.completedTasks

    private var timerJob: Job? = null
    private var sessionStartTime: Long = 0
    private var accumulatedWorkTime: Int = 0

    private val _showProgressDialog = MutableStateFlow(false)
    val showProgressDialog: StateFlow<Boolean> = _showProgressDialog.asStateFlow()

    private val _showCelebrationDialog = MutableStateFlow(false)
    val showCelebrationDialog: StateFlow<Boolean> = _showCelebrationDialog.asStateFlow()

    private val _celebrationSessionType = MutableStateFlow<SessionType?>(null)
    val celebrationSessionType: StateFlow<SessionType?> = _celebrationSessionType.asStateFlow()

    private val _showCoinRewardDialog = MutableStateFlow(false)
    val showCoinRewardDialog: StateFlow<Boolean> = _showCoinRewardDialog.asStateFlow()

    private val _lastCoinReward = MutableStateFlow<CoinReward?>(null)
    val lastCoinReward: StateFlow<CoinReward?> = _lastCoinReward.asStateFlow()

    private var bonusTimeInSeconds = 0

    init {
        resetTimer()
    }

    fun startTimer() {
        if (_timerState.value == TimerState.RUNNING) return

        _timerState.value = TimerState.RUNNING

        if (_sessionType.value == SessionType.WORK) {
            sessionStartTime = System.currentTimeMillis()
        }

        if (_settings.value.soundEnabled) {
            // ← ACTUALIZADO: Pasar el DAO
            viewModelScope.launch {
                musicPlayer.playMusicForSession(
                    _sessionType.value,
                    true,
                    _settings.value.workMusicTrackId,
                    _settings.value.shortBreakMusicTrackId,
                    _settings.value.longBreakMusicTrackId,
                    AppDatabase.getDatabase(getApplication()).importedMusicDao()  // ← NUEVO
                )
            }
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

        if (_sessionType.value == SessionType.WORK && sessionStartTime > 0) {
            val workTime = ((System.currentTimeMillis() - sessionStartTime) / 1000).toInt()
            accumulatedWorkTime += workTime
            sessionStartTime = 0
        }
    }

    fun skipSession() {
        timerJob?.cancel()

        if (_sessionType.value == SessionType.WORK && sessionStartTime > 0) {
            val workTime = ((System.currentTimeMillis() - sessionStartTime) / 1000).toInt()
            accumulatedWorkTime += workTime
            saveAccumulatedTime()
        }

        _timerState.value = TimerState.IDLE
        onTimerComplete()
    }

    private fun onTimerComplete() {
        if (_sessionType.value == SessionType.WORK && sessionStartTime > 0) {
            val workTime = ((System.currentTimeMillis() - sessionStartTime) / 1000).toInt()
            accumulatedWorkTime += workTime
            sessionStartTime = 0

            if (_currentTask.value != null) {
                _showProgressDialog.value = true
                return
            }
        }

        saveAccumulatedTime()
        notificationHelper.showSessionCompleteNotification(_sessionType.value)

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

        _celebrationSessionType.value = _sessionType.value
        _showCelebrationDialog.value = true

        when (_sessionType.value) {
            SessionType.WORK -> {
                _completedPomodoros.value++

                _currentTask.value?.let { task ->
                    viewModelScope.launch {
                        repository.incrementPomodoro(task)
                        userRepository.addCoins(CoinReward.POMODORO_COMPLETED.amount, CoinReward.POMODORO_COMPLETED)
                        showCoinReward(CoinReward.POMODORO_COMPLETED)
                        statsRepository.incrementPomodoro()  // ← NUEVO
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
                bonusTimeInSeconds = 0
            }
        }

        resetTimer()

        if (shouldAutoStart()) {
            startTimer()
        }
    }

    private fun saveAccumulatedTime() {
        _currentTask.value?.let { task ->
            if (accumulatedWorkTime > 0) {
                viewModelScope.launch {
                    repository.addTimeToTask(task, accumulatedWorkTime)
                    statsRepository.addTimeWorked(accumulatedWorkTime)  // ← NUEVO
                    _currentTask.value = task.copy(
                        timeSpentInSeconds = task.timeSpentInSeconds + accumulatedWorkTime
                    )
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
        if (_currentTask.value != null && accumulatedWorkTime > 0) {
            saveAccumulatedTime()
        }

        _currentTask.value = task
        accumulatedWorkTime = 0
    }

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
            userRepository.addCoins(CoinReward.TASK_COMPLETED.amount, CoinReward.TASK_COMPLETED)
            showCoinReward(CoinReward.TASK_COMPLETED)
            statsRepository.incrementTask()  // ← NUEVO

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

    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }

    fun formatWorkTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return if (hours > 0) {
            "${hours}h ${minutes}m"
        } else {
            "${minutes}m"
        }
    }

    fun saveProgressNote(noteText: String) {
        _showProgressDialog.value = false

        val timeToSave = accumulatedWorkTime

        _currentTask.value?.let { task ->
            if (noteText.isNotBlank()) {
                val bonus = calculateBonusTime(noteText.length)
                bonusTimeInSeconds = bonus

                viewModelScope.launch {
                    repository.addProgressNote(task, noteText, timeToSave)
                    val updatedTask = repository.getTaskById(task.id)
                    _currentTask.value = updatedTask

                    userRepository.addCoins(CoinReward.PROGRESS_NOTE.amount, CoinReward.PROGRESS_NOTE)
                    showCoinReward(CoinReward.PROGRESS_NOTE)
                    statsRepository.incrementNote()  // ← NUEVO
                }
            }
        }

        accumulatedWorkTime = 0
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
        notificationHelper.showSessionCompleteNotification(_sessionType.value)

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

        _celebrationSessionType.value = _sessionType.value
        _showCelebrationDialog.value = true

        _completedPomodoros.value++

        _currentTask.value?.let { task ->
            viewModelScope.launch {
                repository.incrementPomodoro(task)
                userRepository.addCoins(CoinReward.POMODORO_COMPLETED.amount, CoinReward.POMODORO_COMPLETED)
                showCoinReward(CoinReward.POMODORO_COMPLETED)
                statsRepository.incrementPomodoro()  // ← NUEVO
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

    private fun showCoinReward(reward: CoinReward) {
        _lastCoinReward.value = reward
        _showCoinRewardDialog.value = true
    }

    fun dismissCoinReward() {
        _showCoinRewardDialog.value = false
        _lastCoinReward.value = null
    }

    fun resetTimer() {
        _timerState.value = TimerState.IDLE
        timerJob?.cancel()
        sessionStartTime = 0

        val baseDuration = when (_sessionType.value) {
            SessionType.WORK -> _settings.value.workDuration * 60
            SessionType.SHORT_BREAK -> _settings.value.shortBreakDuration * 60
            SessionType.LONG_BREAK -> _settings.value.longBreakDuration * 60
        }

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

        if (accumulatedWorkTime > 0) {
            saveAccumulatedTime()
        }
    }
}