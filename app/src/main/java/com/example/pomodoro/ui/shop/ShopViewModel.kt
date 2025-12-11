package com.example.pomodoro.ui.shop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.database.AppDatabase
import com.example.pomodoro.data.repository.MusicRepository
import com.example.pomodoro.data.repository.PurchaseResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val musicRepository: MusicRepository

    init {
        val musicDao = AppDatabase.getDatabase(application).musicDao()
        val userDao = AppDatabase.getDatabase(application).userDao()
        musicRepository = MusicRepository(musicDao, userDao)
    }

    val unlockedMusicIds = musicRepository.unlockedMusicIds

    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()

    fun purchaseTrack(trackId: Int) {
        viewModelScope.launch {
            _purchaseState.value = PurchaseState.Loading

            val result = musicRepository.purchaseTrack(trackId)

            _purchaseState.value = when (result) {
                is PurchaseResult.Success -> PurchaseState.Success(result.track.name)
                is PurchaseResult.InsufficientCoins -> PurchaseState.Error("No tienes suficientes monedas")
                is PurchaseResult.AlreadyUnlocked -> PurchaseState.Error("Ya tienes esta canción")
                is PurchaseResult.TrackNotFound -> PurchaseState.Error("Canción no encontrada")
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
    data class Success(val trackName: String) : PurchaseState()
    data class Error(val message: String) : PurchaseState()
}