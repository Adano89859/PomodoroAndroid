package com.example.pomodoro.data.model

import androidx.annotation.RawRes

data class MusicTrack(
    val id: String,
    val name: String,
    val description: String,
    @RawRes val resourceId: Int,
    val sessionType: SessionType
)