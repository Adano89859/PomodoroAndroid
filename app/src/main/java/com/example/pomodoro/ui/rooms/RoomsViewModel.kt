package com.example.pomodoro.ui.rooms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.database.AppDatabase
import com.example.pomodoro.data.model.RoomType
import com.example.pomodoro.data.repository.PurchaseItemResult
import com.example.pomodoro.data.repository.RoomProgress
import com.example.pomodoro.data.repository.RoomRepository
import com.example.pomodoro.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RoomsViewModel(application: Application) : AndroidViewModel(application) {

    private val roomRepository: RoomRepository
    private val userRepository: UserRepository

    init {
        val roomItemDao = AppDatabase.getDatabase(application).roomItemDao()
        val userDao = AppDatabase.getDatabase(application).userDao()

        roomRepository = RoomRepository(roomItemDao, userDao)
        userRepository = UserRepository(userDao)
    }

    val userCoins = userRepository.user
        .map { it?.coins ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val purchasedItemIds = roomRepository.purchasedItemIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()

    private val _roomProgressMap = MutableStateFlow<Map<RoomType, RoomProgress>>(emptyMap())
    val roomProgressMap: StateFlow<Map<RoomType, RoomProgress>> = _roomProgressMap.asStateFlow()

    init {
        loadAllRoomProgress()
    }

    private fun loadAllRoomProgress() {
        viewModelScope.launch {
            val progressMap = mutableMapOf<RoomType, RoomProgress>()
            RoomType.values().forEach { roomType ->
                progressMap[roomType] = roomRepository.getRoomProgress(roomType)
            }
            _roomProgressMap.value = progressMap
        }
    }

    fun purchaseItem(itemId: Int) {
        viewModelScope.launch {
            _purchaseState.value = PurchaseState.Loading

            val result = roomRepository.purchaseItem(itemId)

            _purchaseState.value = when (result) {
                is PurchaseItemResult.Success -> {
                    loadAllRoomProgress() // Recargar progreso
                    PurchaseState.Success(result.item.name)
                }
                is PurchaseItemResult.InsufficientCoins -> PurchaseState.Error("No tienes suficientes monedas")
                is PurchaseItemResult.AlreadyPurchased -> PurchaseState.Error("Ya tienes este objeto")
                is PurchaseItemResult.ItemNotFound -> PurchaseState.Error("Objeto no encontrado")
            }
        }
    }

    fun dismissPurchaseState() {
        _purchaseState.value = PurchaseState.Idle
    }
}

sealed class PurchaseState {
    object Idle : PurchaseState()
    object Loading : PurchaseState()
    data class Success(val itemName: String) : PurchaseState()
    data class Error(val message: String) : PurchaseState()
}