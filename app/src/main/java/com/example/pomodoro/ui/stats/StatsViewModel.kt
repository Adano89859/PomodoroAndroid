package com.example.pomodoro.ui.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.database.AppDatabase
import com.example.pomodoro.data.model.DailyStats
import com.example.pomodoro.data.model.RoomType
import com.example.pomodoro.data.repository.MusicRepository
import com.example.pomodoro.data.repository.RoomRepository
import com.example.pomodoro.data.repository.StatsRepository
import com.example.pomodoro.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val statsRepository: StatsRepository
    private val userRepository: UserRepository
    private val musicRepository: MusicRepository
    private val roomRepository: RoomRepository  // ← NUEVO

    init {
        val dailyStatsDao = AppDatabase.getDatabase(application).dailyStatsDao()
        val userDao = AppDatabase.getDatabase(application).userDao()
        val musicDao = AppDatabase.getDatabase(application).musicDao()
        val roomItemDao = AppDatabase.getDatabase(application).roomItemDao()  // ← NUEVO

        statsRepository = StatsRepository(dailyStatsDao)
        userRepository = UserRepository(userDao)
        musicRepository = MusicRepository(musicDao, userDao)
        roomRepository = RoomRepository(roomItemDao, userDao)  // ← NUEVO
    }

    val user = userRepository.user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val last7Days = statsRepository.getLast7Days()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unlockedMusicCount = musicRepository.unlockedMusicIds
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _totalStats = MutableStateFlow(TotalStats())
    val totalStats: StateFlow<TotalStats> = _totalStats.asStateFlow()

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    // ← NUEVO: Estadísticas de habitaciones
    private val _roomsStats = MutableStateFlow(RoomsStats())
    val roomsStats: StateFlow<RoomsStats> = _roomsStats.asStateFlow()

    init {
        loadTotalStats()
        loadCurrentStreak()
        loadRoomsStats()  // ← NUEVO
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

    // ← NUEVO: Cargar estadísticas de habitaciones
    private fun loadRoomsStats() {
        viewModelScope.launch {
            val gardenProgress = roomRepository.getRoomProgress(RoomType.GARDEN)
            val officeProgress = roomRepository.getRoomProgress(RoomType.OFFICE)
            val bedroomProgress = roomRepository.getRoomProgress(RoomType.BEDROOM)

            val completedRooms = listOf(gardenProgress, officeProgress, bedroomProgress)
                .count { it.isComplete }

            val totalItems = gardenProgress.totalCount + officeProgress.totalCount + bedroomProgress.totalCount
            val purchasedItems = gardenProgress.purchasedCount + officeProgress.purchasedCount + bedroomProgress.purchasedCount
            val totalPercentage = if (totalItems > 0) (purchasedItems * 100) / totalItems else 0

            _roomsStats.value = RoomsStats(
                completedRooms = completedRooms,
                totalRooms = 3,
                purchasedItems = purchasedItems,
                totalItems = totalItems,
                totalPercentage = totalPercentage
            )
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

// ← NUEVO: Data class para estadísticas de habitaciones
data class RoomsStats(
    val completedRooms: Int = 0,
    val totalRooms: Int = 3,
    val purchasedItems: Int = 0,
    val totalItems: Int = 0,
    val totalPercentage: Int = 0
)