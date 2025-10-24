package com.example.gamezone30.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gamezone30.data.local.dao.entity.User

@Dao
interface UserDao {

    /**
     * Inserta un nuevo usuario, y si se intenta insertar un email q ya existe
     * lanzará un error.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    /**
     * Busca un usuario por su email.
     */
    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    suspend fun findUserByEmail(email: String): User?
    fun insert(user: User)
}