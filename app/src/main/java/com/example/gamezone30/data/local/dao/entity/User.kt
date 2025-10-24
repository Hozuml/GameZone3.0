package com.example.gamezone30.data.local.dao.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    val email: String, // Usamos el email como ID único

    val fullName: String,

    val password: String,

    val phone: String?, // '?' significa que es opcional (puede ser null)

    val favoriteGenres: List<String>
)