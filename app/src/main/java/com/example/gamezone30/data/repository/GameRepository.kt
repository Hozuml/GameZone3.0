package com.example.gamezone30.data.repository

import com.example.gamezone30.data.local.dao.entity.Game
import com.example.gamezone30.network.ApiService
import com.example.gamezone30.network.ExternalGameListResponse
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

    // Función para consumir la API externa (RAWG)
    suspend fun getExternalGameNews(): Result<ExternalGameListResponse> {
        // Por seguridad, este dato debería venir de un BuildConfig o DataStore, pero lo dejamos aquí.
        val apiKey = "TU_CLAVE_API_RAWG"

        // La URL busca los juegos mejor rankeados recientemente
        val url = "https://api.rawg.io/api/games?key=$apiKey&dates=2024-01-01,2024-12-31&ordering=-added&page_size=3"

        return try {
            val response = api.getExternalGames(fullUrl = url)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al conectar con RAWG: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}