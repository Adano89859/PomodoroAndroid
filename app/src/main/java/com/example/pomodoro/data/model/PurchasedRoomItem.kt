package com.example.pomodoro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchased_room_items")
data class PurchasedRoomItem(
    @PrimaryKey val itemId: Int
)