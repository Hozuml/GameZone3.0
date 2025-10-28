package com.example.gamezone30.data.local.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    val email: String,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    val password: String,

    val phone: String?,

    val favoriteGenres: List<String>
)
