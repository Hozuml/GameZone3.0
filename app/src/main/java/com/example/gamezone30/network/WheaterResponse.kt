package com.example.gamezone30.network

data class WeatherResponse(
    val name: String,
    val main: Main,   // Contiene la temperatura
    val weather: List<Weather> // Contiene la descripción
)

data class Main(
    val temp: Double
)

data class Weather(
    val description: String
)