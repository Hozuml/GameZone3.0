package com.example.gamezone30.data.local.dao.entity


data class Game(
    val id: Long? = null,
    val title: String,
    val genre: String,
    val price: Double,
    val description: String?
)