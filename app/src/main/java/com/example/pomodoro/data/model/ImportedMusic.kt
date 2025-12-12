package com.example.pomodoro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "imported_music")
data class ImportedMusic(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val displayName: String,           // Nombre que muestra el usuario
    val originalFileName: String,      // Nombre del archivo original
    val internalFilePath: String,      // Ruta donde guardamos el archivo
    val sessionType: SessionType,      // Para qué tipo de sesión
    val isPurchased: Boolean = false,  // Si ya pagó las 150 monedas
    val durationSeconds: Int = 0       // Duración del audio
)