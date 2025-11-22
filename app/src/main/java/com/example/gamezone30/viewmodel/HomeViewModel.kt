package com.example.gamezone30.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamezone30.data.local.dao.entity.Game
import com.example.gamezone30.data.repository.GameRepository
import com.example.gamezone30.data.session.SessionPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val localGameList: List<Game> = emptyList(), // Juegos creados
    val externalNews: List<Game> = emptyList(), // Juegos de la API de RAWG
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val gameRepository: GameRepository,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadGames()
        loadExternalNews() // NUEVO: Cargamos también los datos externos
    }

    private fun loadGames() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = gameRepository.getAllGames() // Llama a tu servidor Spring Boot

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(localGameList = result.getOrNull() ?: emptyList(), isLoading = false)
                }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Error de conexión con el servidor interno"
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
            }
        }
    }

    private fun loadExternalNews() {
        viewModelScope.launch {
            // Llamamos a la API externa
            val result = gameRepository.getExternalGameNews()

            if (result.isSuccess) {
                // Si funciona, guardamos la lista de juegos externos
                _uiState.update {
                    it.copy(externalNews = result.getOrNull()?.games ?: emptyList())
                }
            } else {
                // Si falla la API externa, no detenemos la carga del home, solo mostramos un error en esa sección
                println("DEBUG: Error al cargar noticias externas: ${result.exceptionOrNull()?.message}")
            }
        }
    }
}

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