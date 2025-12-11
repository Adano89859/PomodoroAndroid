package com.example.pomodoro.data.model

import androidx.annotation.RawRes

data class MusicTrack(
    val id: Int,                    // Cambiar String → Int
    val name: String,
    val description: String,
    @RawRes val resourceId: Int,
    val sessionType: SessionType,
    val emoji: String = "🎵",       // NUEVO - emoji por defecto
    val price: Int = 0              // NUEVO - 0 = gratis, 75 = de pago
)