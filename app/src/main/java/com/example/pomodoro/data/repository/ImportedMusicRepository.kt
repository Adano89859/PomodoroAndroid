package com.example.pomodoro.data.repository

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.pomodoro.data.database.ImportedMusicDao
import com.example.pomodoro.data.database.UserDao
import com.example.pomodoro.data.model.ImportedMusic
import com.example.pomodoro.data.model.SessionType
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream

class ImportedMusicRepository(
    private val importedMusicDao: ImportedMusicDao,
    private val userDao: UserDao,
    private val context: Context
) {

    companion object {
        const val MAX_IMPORTED_MUSIC = 15
        const val IMPORT_MUSIC_PRICE = 150
    }

    val allImportedMusic: Flow<List<ImportedMusic>> = importedMusicDao.getAllImportedMusic()

    fun getImportedMusicByType(sessionType: SessionType): Flow<List<ImportedMusic>> {
        return importedMusicDao.getImportedMusicByType(sessionType)
    }

    fun getPurchasedMusicByType(sessionType: SessionType): Flow<List<ImportedMusic>> {
        return importedMusicDao.getPurchasedMusicByType(sessionType)
    }

    suspend fun canImportMore(): Boolean {
        return importedMusicDao.getImportedMusicCount() < MAX_IMPORTED_MUSIC
    }

    suspend fun importMusic(
        uri: Uri,
        displayName: String,
        sessionType: SessionType
    ): ImportMusicResult {
        // Verificar límite
        if (!canImportMore()) {
            return ImportMusicResult.LimitReached
        }

        try {
            // Copiar archivo a almacenamiento interno
            val musicDir = File(context.filesDir, "imported_music")
            if (!musicDir.exists()) {
                musicDir.mkdirs()
            }

            val timestamp = System.currentTimeMillis()
            val extension = getFileExtension(uri) ?: "mp3"
            val fileName = "imported_${timestamp}.$extension"
            val destFile = File(musicDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }

            // Obtener duración del audio
            val duration = getAudioDuration(destFile.absolutePath)

            // Guardar en BD
            val importedMusic = ImportedMusic(
                displayName = displayName,
                originalFileName = getOriginalFileName(uri),
                internalFilePath = destFile.absolutePath,
                sessionType = sessionType,
                isPurchased = false,
                durationSeconds = duration
            )

            val id = importedMusicDao.insertImportedMusic(importedMusic)

            return ImportMusicResult.Success(importedMusic.copy(id = id.toInt()))

        } catch (e: Exception) {
            e.printStackTrace()
            return ImportMusicResult.Error(e.message ?: "Error al importar música")
        }
    }

    suspend fun purchaseImportedMusic(musicId: Int): PurchaseImportedMusicResult {
        val music = importedMusicDao.getImportedMusicById(musicId)
            ?: return PurchaseImportedMusicResult.MusicNotFound

        if (music.isPurchased) {
            return PurchaseImportedMusicResult.AlreadyPurchased
        }

        val user = userDao.getUserOnce() ?: return PurchaseImportedMusicResult.InsufficientCoins

        if (user.coins < IMPORT_MUSIC_PRICE) {
            return PurchaseImportedMusicResult.InsufficientCoins
        }

        // Descontar monedas y desbloquear
        userDao.subtractCoins(IMPORT_MUSIC_PRICE)
        importedMusicDao.purchaseMusic(musicId)

        return PurchaseImportedMusicResult.Success(music.displayName)
    }

    suspend fun deleteImportedMusic(music: ImportedMusic) {
        // Eliminar archivo físico
        val file = File(music.internalFilePath)
        if (file.exists()) {
            file.delete()
        }

        // Eliminar de BD
        importedMusicDao.deleteImportedMusic(music)
    }

    private fun getFileExtension(uri: Uri): String? {
        val fileName = getOriginalFileName(uri)
        return fileName.substringAfterLast('.', "")
    }

    private fun getOriginalFileName(uri: Uri): String {
        var result = "audio.mp3"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                result = cursor.getString(nameIndex)
            }
        }
        return result
    }

    private fun getAudioDuration(filePath: String): Int {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            (duration?.toLongOrNull() ?: 0L) / 1000 // Convertir a segundos
        } catch (e: Exception) {
            0
        }.toInt()
    }
}

sealed class ImportMusicResult {
    data class Success(val music: ImportedMusic) : ImportMusicResult()
    object LimitReached : ImportMusicResult()
    data class Error(val message: String) : ImportMusicResult()
}

sealed class PurchaseImportedMusicResult {
    data class Success(val musicName: String) : PurchaseImportedMusicResult()
    object InsufficientCoins : PurchaseImportedMusicResult()
    object AlreadyPurchased : PurchaseImportedMusicResult()
    object MusicNotFound : PurchaseImportedMusicResult()
}