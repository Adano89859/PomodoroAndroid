package com.example.pomodoro.ui.importedmusic

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.database.AppDatabase
import com.example.pomodoro.data.model.ImportedMusic
import com.example.pomodoro.data.model.SessionType
import com.example.pomodoro.data.repository.ImportMusicResult
import com.example.pomodoro.data.repository.ImportedMusicRepository
import com.example.pomodoro.data.repository.PurchaseImportedMusicResult
import com.example.pomodoro.data.repository.UserRepository
import com.example.pomodoro.utils.MusicPlayer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ImportedMusicViewModel(application: Application) : AndroidViewModel(application) {

    private val importedMusicRepository: ImportedMusicRepository
    private val userRepository: UserRepository
    private val musicPlayer = MusicPlayer(application)

    init {
        val importedMusicDao = AppDatabase.getDatabase(application).importedMusicDao()
        val userDao = AppDatabase.getDatabase(application).userDao()

        importedMusicRepository = ImportedMusicRepository(importedMusicDao, userDao, application)
        userRepository = UserRepository(userDao)
    }

    val userCoins = userRepository.user
        .map { it?.coins ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allImportedMusic = importedMusicRepository.allImportedMusic
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState.asStateFlow()

    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()

    private val _previewingMusicId = MutableStateFlow<Int?>(null)
    val previewingMusicId: StateFlow<Int?> = _previewingMusicId.asStateFlow()

    private val _canImportMore = MutableStateFlow(true)
    val canImportMore: StateFlow<Boolean> = _canImportMore.asStateFlow()

    init {
        checkImportLimit()
    }

    private fun checkImportLimit() {
        viewModelScope.launch {
            _canImportMore.value = importedMusicRepository.canImportMore()
        }
    }

    fun importMusic(uri: Uri, displayName: String, sessionType: SessionType) {
        viewModelScope.launch {
            _importState.value = ImportState.Loading

            val result = importedMusicRepository.importMusic(uri, displayName, sessionType)

            _importState.value = when (result) {
                is ImportMusicResult.Success -> {
                    checkImportLimit()
                    ImportState.Success(result.music.displayName)
                }
                is ImportMusicResult.LimitReached -> ImportState.Error("Has alcanzado el límite de 15 canciones")
                is ImportMusicResult.Error -> ImportState.Error(result.message)
            }
        }
    }

    fun purchaseMusic(musicId: Int) {
        viewModelScope.launch {
            _purchaseState.value = PurchaseState.Loading

            val result = importedMusicRepository.purchaseImportedMusic(musicId)

            _purchaseState.value = when (result) {
                is PurchaseImportedMusicResult.Success -> PurchaseState.Success(result.musicName)
                is PurchaseImportedMusicResult.InsufficientCoins -> PurchaseState.Error("No tienes suficientes monedas")
                is PurchaseImportedMusicResult.AlreadyPurchased -> PurchaseState.Error("Ya has comprado esta canción")
                is PurchaseImportedMusicResult.MusicNotFound -> PurchaseState.Error("Canción no encontrada")
            }
        }
    }

    fun deleteMusic(music: ImportedMusic) {
        viewModelScope.launch {
            importedMusicRepository.deleteImportedMusic(music)
            checkImportLimit()
        }
    }

    fun playPreview(music: ImportedMusic) {
        if (_previewingMusicId.value == music.id) {
            stopPreview()
        } else {
            _previewingMusicId.value = music.id
            // Reproducir desde archivo interno
            musicPlayer.playPreviewFromFile(music.internalFilePath) {
                _previewingMusicId.value = null
            }
        }
    }

    fun stopPreview() {
        musicPlayer.stopPreview()
        _previewingMusicId.value = null
    }

    fun dismissImportState() {
        _importState.value = ImportState.Idle
    }

    fun dismissPurchaseState() {
        _purchaseState.value = PurchaseState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayer.stop()
    }
}

sealed class ImportState {
    object Idle : ImportState()
    object Loading : ImportState()
    data class Success(val musicName: String) : ImportState()
    data class Error(val message: String) : ImportState()
}

sealed class PurchaseState {
    object Idle : PurchaseState()
    object Loading : PurchaseState()
    data class Success(val musicName: String) : PurchaseState()
    data class Error(val message: String) : PurchaseState()
}