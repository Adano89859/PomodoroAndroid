package com.example.pomodoro.data.repository

import com.example.pomodoro.data.database.MusicDao
import com.example.pomodoro.data.database.UserDao
import com.example.pomodoro.data.model.MusicTrack
import com.example.pomodoro.data.model.UnlockedMusic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.pomodoro.utils.MusicCatalog

class MusicRepository(
    private val musicDao: MusicDao,
    private val userDao: UserDao
) {

    val unlockedMusicIds: Flow<List<Int>> = musicDao.getAllUnlockedMusic()
        .map { list -> list.map { it.trackId } }

    suspend fun isTrackUnlocked(trackId: Int): Boolean {
        return musicDao.isUnlocked(trackId) > 0
    }

    suspend fun purchaseTrack(trackId: Int): PurchaseResult {
        val track = MusicCatalog.getTrackById(trackId)
            ?: return PurchaseResult.TrackNotFound

        // Verificar si ya está desbloqueada
        if (isTrackUnlocked(trackId)) {
            return PurchaseResult.AlreadyUnlocked
        }

        // Verificar monedas suficientes
        val user = userDao.getUserOnce() ?: return PurchaseResult.InsufficientCoins
        if (user.coins < track.price) {
            return PurchaseResult.InsufficientCoins
        }

        // Realizar compra
        userDao.subtractCoins(track.price)
        musicDao.unlockTrack(UnlockedMusic(trackId))

        return PurchaseResult.Success(track)
    }

    fun getUnlockedTracksByType(sessionType: com.example.pomodoro.data.model.SessionType): Flow<List<MusicTrack>> {
        return unlockedMusicIds.map { unlockedIds ->
            MusicCatalog.getTracksByType(sessionType)
                .filter { it.id in unlockedIds }
        }
    }
}

sealed class PurchaseResult {
    data class Success(val track: MusicTrack) : PurchaseResult()
    object InsufficientCoins : PurchaseResult()
    object AlreadyUnlocked : PurchaseResult()
    object TrackNotFound : PurchaseResult()
}