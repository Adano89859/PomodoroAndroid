package com.example.pomodoro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unlocked_music")
data class UnlockedMusic(
    @PrimaryKey val trackId: Int
)