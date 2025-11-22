package com.example.gamezone30.data.repository

import android.util.Log
import com.example.gamezone30.data.local.dao.entity.User
import com.example.gamezone30.network.ApiService
import com.example.gamezone30.network.RetrofitClient

class UserRepository {

    private val api: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    // Registro
    suspend fun registerUser(user: User): Result<User> {
        return try {
            val response = api.registrarUsuario(user)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception(response.errorBody()?.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login
    suspend fun login(email: String, pass: String): Result<User> {
        return try {
            val credenciales = mapOf("email" to email, "password" to pass)
            val response = api.login(credenciales)
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception("Error login"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- NUEVO: Actualizar Usuario ---
    suspend fun updateUser(id: Long, user: User): Result<User> {
        return try {
            val response = api.actualizarUsuario(id, user)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al actualizar"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}