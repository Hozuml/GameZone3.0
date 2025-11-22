package com.example.gamezone30.network

import com.example.gamezone30.data.local.dao.entity.Game
import com.example.gamezone30.data.local.dao.entity.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiService {

    @GET("users")
    suspend fun obtenerUsuarios(): Response<List<User>>

    @POST("users/register")
    suspend fun registrarUsuario(@Body user: User): Response<User>

    @POST("users/login")
    suspend fun login(@Body credenciales: Map<String, String>): Response<User>

    @PUT("users/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Long, @Body user: User): Response<User>

    @GET("games")
    suspend fun obtenerJuegos(): Response<List<Game>>

    @GET
    suspend fun getExternalGames(@Url fullUrl: String): Response<ExternalGameListResponse>
}