package com.example.gamezone30.data.local.dao.entity // Tu paquete de Android

import com.google.gson.annotations.SerializedName



data class User(
    // El servidor nos enviará un ID, así que lo agregamos (puede ser nulo al registrar)
    val id: Long? = null,

    val email: String,

    val fullName: String,

    val password: String,

    val phone: String?,

    // AQUI ESTÁ LA MAGIA:
    // Le decimos: "En Kotlin llámate 'favoriteGenres', pero
    // cuando hables con el servidor (JSON), usa el nombre 'generos'"
    @SerializedName("generos")
    val favoriteGenres: List<String> = emptyList()
)