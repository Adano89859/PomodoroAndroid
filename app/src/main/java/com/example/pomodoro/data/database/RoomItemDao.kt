package com.example.pomodoro.data.database

import androidx.room.*
import com.example.pomodoro.data.model.PurchasedRoomItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomItemDao {

    @Query("SELECT * FROM purchased_room_items")
    fun getAllPurchasedItems(): Flow<List<PurchasedRoomItem>>

    @Query("SELECT COUNT(*) FROM purchased_room_items WHERE itemId = :itemId")
    suspend fun isPurchased(itemId: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun purchaseItem(item: PurchasedRoomItem)

    @Query("SELECT COUNT(*) FROM purchased_room_items WHERE itemId IN (:itemIds)")
    suspend fun getPurchasedCount(itemIds: List<Int>): Int
}