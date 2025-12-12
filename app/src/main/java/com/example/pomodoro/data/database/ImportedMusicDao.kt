package com.example.pomodoro.data.database

import androidx.room.*
import com.example.pomodoro.data.model.ImportedMusic
import com.example.pomodoro.data.model.SessionType
import kotlinx.coroutines.flow.Flow

@Dao
interface ImportedMusicDao {

    @Query("SELECT * FROM imported_music ORDER BY id DESC")
    fun getAllImportedMusic(): Flow<List<ImportedMusic>>

    @Query("SELECT * FROM imported_music WHERE sessionType = :sessionType")
    fun getImportedMusicByType(sessionType: SessionType): Flow<List<ImportedMusic>>

    @Query("SELECT * FROM imported_music WHERE isPurchased = 1 AND sessionType = :sessionType")
    fun getPurchasedMusicByType(sessionType: SessionType): Flow<List<ImportedMusic>>

    @Query("SELECT COUNT(*) FROM imported_music")
    suspend fun getImportedMusicCount(): Int

    @Query("SELECT * FROM imported_music WHERE id = :id")
    suspend fun getImportedMusicById(id: Int): ImportedMusic?

    @Insert
    suspend fun insertImportedMusic(music: ImportedMusic): Long

    @Update
    suspend fun updateImportedMusic(music: ImportedMusic)

    @Delete
    suspend fun deleteImportedMusic(music: ImportedMusic)

    @Query("UPDATE imported_music SET isPurchased = 1 WHERE id = :id")
    suspend fun purchaseMusic(id: Int)
}