package com.example.pomodoro.data.repository

import com.example.pomodoro.data.database.RoomItemDao
import com.example.pomodoro.data.database.UserDao
import com.example.pomodoro.data.model.PurchasedRoomItem
import com.example.pomodoro.data.model.RoomCatalog
import com.example.pomodoro.data.model.RoomItem
import com.example.pomodoro.data.model.RoomType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomRepository(
    private val roomItemDao: RoomItemDao,
    private val userDao: UserDao
) {

    val purchasedItemIds: Flow<List<Int>> = roomItemDao.getAllPurchasedItems()
        .map { list -> list.map { it.itemId } }

    suspend fun isPurchased(itemId: Int): Boolean {
        return roomItemDao.isPurchased(itemId) > 0
    }

    suspend fun purchaseItem(itemId: Int): PurchaseItemResult {
        val item = RoomCatalog.getItemById(itemId)
            ?: return PurchaseItemResult.ItemNotFound

        if (isPurchased(itemId)) {
            return PurchaseItemResult.AlreadyPurchased
        }

        val user = userDao.getUserOnce() ?: return PurchaseItemResult.InsufficientCoins
        if (user.coins < item.price) {
            return PurchaseItemResult.InsufficientCoins
        }

        userDao.subtractCoins(item.price)
        roomItemDao.purchaseItem(PurchasedRoomItem(itemId))

        return PurchaseItemResult.Success(item)
    }

    suspend fun getRoomProgress(roomType: RoomType): RoomProgress {
        val items = RoomCatalog.getItemsByRoom(roomType)
        val itemIds = items.map { it.id }
        val purchasedCount = roomItemDao.getPurchasedCount(itemIds)
        val totalCount = items.size
        val percentage = if (totalCount > 0) (purchasedCount * 100) / totalCount else 0

        return RoomProgress(
            roomType = roomType,
            purchasedCount = purchasedCount,
            totalCount = totalCount,
            percentage = percentage,
            isComplete = purchasedCount == totalCount
        )
    }
}

sealed class PurchaseItemResult {
    data class Success(val item: RoomItem) : PurchaseItemResult()
    object InsufficientCoins : PurchaseItemResult()
    object AlreadyPurchased : PurchaseItemResult()
    object ItemNotFound : PurchaseItemResult()
}

data class RoomProgress(
    val roomType: RoomType,
    val purchasedCount: Int,
    val totalCount: Int,
    val percentage: Int,
    val isComplete: Boolean
)