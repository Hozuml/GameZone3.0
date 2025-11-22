package com.example.gamezone30.network

import com.example.gamezone30.data.local.dao.entity.Game
import com.google.gson.annotations.SerializedName

data class ExternalGameListResponse(
    val count: Int,
    @SerializedName("results")
    val games: List<Game>
)