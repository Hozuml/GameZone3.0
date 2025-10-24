package com.example.gamezone30.data.local.dao.database

import androidx.room.TypeConverter

class Converters {

    /**
     * Convierte una Lista de Géneros (ej: ["Acción", "RPG"])
     * en un simple String (ej: "Acción,RPG") para guardarlo.
     */
    @TypeConverter
    fun fromGenreList(genres: List<String>): String {
        return genres.joinToString(separator = ",")
    }

    /**
     * Convierte un String (ej: "Acción,RPG") de vuelta
     * a una Lista (ej: ["Acción", "RPG"]) al leerlo.
     */
    @TypeConverter
    fun toGenreList(data: String): List<String> {
        // "split" hace lo opuesto
        return data.split(",")
    }
}