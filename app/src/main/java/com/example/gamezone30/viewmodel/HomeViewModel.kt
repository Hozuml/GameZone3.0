package com.example.gamezone30.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamezone30.data.local.dao.entity.Game
import com.example.gamezone30.data.repository.GameRepository
import com.example.gamezone30.data.session.SessionPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la pantalla de Inicio
data class HomeUiState(
    val localGameList: List<Game> = emptyList(), // Catálogo del servidor propio
    val weatherInfo: String = "Cargando clima...", // Dato de la API Externa
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val gameRepository: GameRepository,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Al abrir la pantalla, cargamos ambas cosas
        loadGames()
        loadWeather()
    }

    // --- PUNTO I: Microservicio Propio (Spring Boot) ---
    private fun loadGames() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val result = gameRepository.getAllGames()
                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            localGameList = result.getOrNull() ?: emptyList(),
                            isLoading = false
                        )
                    }
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error de conexión interno"
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = errorMsg)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    // --- PUNTO J: API Externa (OpenWeatherMap) ---
    private fun loadWeather() {
        viewModelScope.launch {
            println("DEBUG: Intentando conectar a OpenWeatherMap...") // <--- CHISMOSO 1

            val result = gameRepository.getWeatherForSantiago()

            if (result.isSuccess) {
                val data = result.getOrNull()
                println("DEBUG: ¡Éxito! Clima recibido: ${data?.name}") // <--- CHISMOSO 2

                val temp = data?.main?.temp?.toInt()
                val desc = data?.weather?.firstOrNull()?.description

                val weatherText = if (data != null && temp != null) {
                    "Clima en ${data.name}: $temp°C - $desc"
                } else {
                    "Datos de clima incompletos"
                }

                _uiState.update { it.copy(weatherInfo = weatherText) }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                println("DEBUG: Falló la API de Clima: $error") // <--- CHISMOSO 3

                _uiState.update { it.copy(weatherInfo = "No se pudo cargar el clima.") }
            }
        }
    }
}

// Factory para inyectar los repositorios
class HomeViewModelFactory(
    private val gameRepository: GameRepository,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(gameRepository, sessionPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}