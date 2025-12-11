package com.example.pomodoro.data.database

import androidx.room.*
import com.example.pomodoro.data.model.UnlockedMusic
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Query("SELECT * FROM unlocked_music")
    fun getAllUnlockedMusic(): Flow<List<UnlockedMusic>>

    @Query("SELECT * FROM unlocked_music WHERE trackId = :trackId LIMIT 1")
    suspend fun isTrackUnlocked(trackId: Int): UnlockedMusic?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockTrack(unlockedMusic: UnlockedMusic)

    @Query("SELECT COUNT(*) FROM unlocked_music WHERE trackId = :trackId")
    suspend fun isUnlocked(trackId: Int): Int
}