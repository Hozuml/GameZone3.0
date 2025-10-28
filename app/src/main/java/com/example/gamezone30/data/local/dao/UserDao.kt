package com.example.gamezone30.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gamezone30.data.local.dao.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    suspend fun findUserByEmail(email: String): User?

    @Query("SELECT * FROM user_table LIMIT 1")
    fun getUser(): Flow<User?>

    @Query("UPDATE user_table SET full_name = :fullName, phone = :phone")
    suspend fun updateUser(fullName: String, phone: String)
}