package com.example.pomodoro.data.database

import androidx.room.*
import com.example.pomodoro.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = 1 LIMIT 1")
    fun getUser(): Flow<User?>

    @Query("SELECT * FROM user WHERE id = 1 LIMIT 1")
    suspend fun getUserOnce(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE user SET coins = coins + :amount WHERE id = 1")
    suspend fun addCoins(amount: Int)

    @Query("UPDATE user SET coins = coins - :amount WHERE id = 1")
    suspend fun subtractCoins(amount: Int)
}