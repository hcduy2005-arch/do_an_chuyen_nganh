package com.example.doanckapi.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity): Long // Returns -1 if conflict

    @Query("SELECT * FROM users_table WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users_table WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM users_table")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("DELETE FROM users_table WHERE id = :id")
    suspend fun deleteUserById(id: Int)
}
