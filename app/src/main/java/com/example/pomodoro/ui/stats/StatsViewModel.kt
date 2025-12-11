package com.example.pomodoro.ui.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.database.AppDatabase
import com.example.pomodoro.data.model.DailyStats
import com.example.pomodoro.data.repository.MusicRepository
import com.example.pomodoro.data.repository.StatsRepository
import com.example.pomodoro.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val statsRepository: StatsRepository
    private val userRepository: UserRepository
    private val musicRepository: MusicRepository

    init {
        val dailyStatsDao = AppDatabase.getDatabase(application).dailyStatsDao()
        val userDao = AppDatabase.getDatabase(application).userDao()
        val musicDao = AppDatabase.getDatabase(application).musicDao()

        statsRepository = StatsRepository(dailyStatsDao)
        userRepository = UserRepository(userDao)
        musicRepository = MusicRepository(musicDao, userDao)
    }

    // Flow de usuario (monedas y totales)
    val user = userRepository.user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Estadísticas de los últimos 7 días
    val last7Days = statsRepository.getLast7Days()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Canciones desbloqueadas
    val unlockedMusicCount = musicRepository.unlockedMusicIds
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Totales acumulados
    private val _totalStats = MutableStateFlow(TotalStats())
    val totalStats: StateFlow<TotalStats> = _totalStats.asStateFlow()

    // Racha actual
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    init {
        loadTotalStats()
        loadCurrentStreak()
    }

    private fun loadTotalStats() {
        viewModelScope.launch {
            val pomodoros = statsRepository.getTotalPomodoros()
            val tasks = statsRepository.getTotalTasks()
            val notes = statsRepository.getTotalNotes()
            val timeWorked = statsRepository.getTotalTimeWorked()

            _totalStats.value = TotalStats(
                totalPomodoros = pomodoros,
                totalTasks = tasks,
                totalNotes = notes,
                totalTimeWorked = timeWorked
            )
        }
    }

    private fun loadCurrentStreak() {
        viewModelScope.launch {
            _currentStreak.value = statsRepository.getCurrentStreak()
        }
    }

    fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "${seconds}s"
        }
    }
}

data class TotalStats(
    val totalPomodoros: Int = 0,
    val totalTasks: Int = 0,
    val totalNotes: Int = 0,
    val totalTimeWorked: Int = 0
)