package com.example.gamezone30.data.repository

import com.example.gamezone30.data.local.dao.entity.Game
import com.example.gamezone30.network.ApiService
import com.example.gamezone30.network.RetrofitClient
import com.example.gamezone30.network.WeatherResponse
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
            Result.failure(Exception("Error de conexión al microservicio: Servidor inactivo."))
        }
    }

    // --- API EXTERNA (OpenWeatherMap) ---

    suspend fun getWeatherForSantiago(): Result<WeatherResponse> {
        val apiKey = "tu_clave_real_aqui" // <--- ¡Pega tu clave real aquí!

        // Construimos la URL completa a mano para salirnos del localhost
        val fullUrl = "https://api.openweathermap.org/data/2.5/weather?lat=-33.4489&lon=-70.6693&appid=$apiKey&units=metric"

        return try {
            // Llamamos usando la URL completa
            val response = api.getWeather(url = fullUrl)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener clima: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- FUNCIONES CRUD ---

    suspend fun createGame(game: Game): Result<Game> {
        return try {
            val response = api.createGame(game)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al crear juego."
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: servidor inactivo."))
        }
    }
}