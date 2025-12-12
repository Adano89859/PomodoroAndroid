package com.example.pomodoro.ui.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.database.AppDatabase
import com.example.pomodoro.data.model.Achievement
import com.example.pomodoro.data.model.AchievementCatalog
import com.example.pomodoro.data.model.AchievementCategory
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
    private val roomRepository: RoomRepository

    init {
        val dailyStatsDao = AppDatabase.getDatabase(application).dailyStatsDao()
        val userDao = AppDatabase.getDatabase(application).userDao()
        val musicDao = AppDatabase.getDatabase(application).musicDao()
        val roomItemDao = AppDatabase.getDatabase(application).roomItemDao()

        statsRepository = StatsRepository(dailyStatsDao)
        userRepository = UserRepository(userDao)
        musicRepository = MusicRepository(musicDao, userDao)
        roomRepository = RoomRepository(roomItemDao, userDao)
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

    private val _roomsStats = MutableStateFlow(RoomsStats())
    val roomsStats: StateFlow<RoomsStats> = _roomsStats.asStateFlow()

    private val _unlockedAchievements = MutableStateFlow<List<Achievement>>(emptyList())
    val unlockedAchievements: StateFlow<List<Achievement>> = _unlockedAchievements.asStateFlow()

    private val _weeklyComparison = MutableStateFlow<WeeklyComparison?>(null)
    val weeklyComparison: StateFlow<WeeklyComparison?> = _weeklyComparison.asStateFlow()

    private val _bestDay = MutableStateFlow<DailyStats?>(null)
    val bestDay: StateFlow<DailyStats?> = _bestDay.asStateFlow()

    init {
        loadTotalStats()
        loadCurrentStreak()
        loadRoomsStats()
        loadWeeklyComparison()
        loadBestDay()

        // ← NUEVO: Recargar logros cada vez que cambian las estadísticas
        viewModelScope.launch {
            _totalStats.collect {
                loadAchievements()
            }
        }

        viewModelScope.launch {
            _currentStreak.collect {
                loadAchievements()
            }
        }

        viewModelScope.launch {
            unlockedMusicCount.collect {
                loadAchievements()
            }
        }

        viewModelScope.launch {
            _roomsStats.collect {
                loadAchievements()
            }
        }
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

    private fun loadAchievements() {
        viewModelScope.launch {
            val totalStats = _totalStats.value
            val streak = _currentStreak.value
            val musicCount = unlockedMusicCount.value
            val roomsCompleted = _roomsStats.value.completedRooms

            val unlocked = mutableListOf<Achievement>()

            AchievementCatalog.achievements.forEach { achievement ->
                val isUnlocked = when (achievement.category) {
                    AchievementCategory.POMODOROS -> totalStats.totalPomodoros >= achievement.requirement
                    AchievementCategory.TASKS -> totalStats.totalTasks >= achievement.requirement
                    AchievementCategory.NOTES -> totalStats.totalNotes >= achievement.requirement
                    AchievementCategory.STREAK -> streak >= achievement.requirement
                    AchievementCategory.MUSIC -> musicCount >= achievement.requirement
                    AchievementCategory.ROOMS -> roomsCompleted >= achievement.requirement
                }

                if (isUnlocked) {
                    unlocked.add(achievement)
                }
            }

            _unlockedAchievements.value = unlocked
        }
    }

    private fun loadWeeklyComparison() {
        viewModelScope.launch {
            val last7 = last7Days.value
            if (last7.isEmpty()) {
                _weeklyComparison.value = null
                return@launch
            }

            val thisWeekPomodoros = last7.sumOf { it.pomodorosCompleted }
            val thisWeekTasks = last7.sumOf { it.tasksCompleted }
            val thisWeekTime = last7.sumOf { it.timeWorkedInSeconds }

            val avgPomodorosPerDay = if (last7.isNotEmpty()) thisWeekPomodoros / last7.size else 0

            _weeklyComparison.value = WeeklyComparison(
                thisWeekPomodoros = thisWeekPomodoros,
                thisWeekTasks = thisWeekTasks,
                thisWeekTime = thisWeekTime,
                avgPomodorosPerDay = avgPomodorosPerDay
            )
        }
    }

    private fun loadBestDay() {
        viewModelScope.launch {
            val last7 = last7Days.value
            _bestDay.value = last7.maxByOrNull { it.pomodorosCompleted }
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

data class RoomsStats(
    val completedRooms: Int = 0,
    val totalRooms: Int = 3,
    val purchasedItems: Int = 0,
    val totalItems: Int = 0,
    val totalPercentage: Int = 0
)

data class WeeklyComparison(
    val thisWeekPomodoros: Int,
    val thisWeekTasks: Int,
    val thisWeekTime: Int,
    val avgPomodorosPerDay: Int
)