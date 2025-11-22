package com.example.gamezone30.data.repository

import com.example.gamezone30.data.local.dao.entity.Game
import com.example.gamezone30.network.ApiService
import com.example.gamezone30.network.RetrofitClient
import retrofit2.HttpException

class GameRepository {
    private val api: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    suspend fun getAllGames(): Result<List<Game>> {
        return try {
            val response = api.obtenerJuegos()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}