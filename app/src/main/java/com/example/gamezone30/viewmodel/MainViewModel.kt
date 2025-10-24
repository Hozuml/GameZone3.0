package com.example.gamezone30.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamezone30.data.session.SessionPreferencesRepository
import com.example.gamezone30.navigation.AppScreens
import com.example.gamezone30.navigation.NavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class MainViewModel(
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    /**
     * La UI llama a esta función para pedir una navegación.
     */
    fun navigateTo(
        route: AppScreens,
        popUpToRoute: AppScreens? = null,
        inclusive: Boolean = false,
        singleTop: Boolean = false
    ) {
        viewModelScope.launch {
            _navigationEvents.emit(
                NavigationEvent.NavigateTo(
                    route = route,
                    popUpToRoute = popUpToRoute,
                    inclusive = inclusive,
                    singleTop = singleTop
                )
            )
        }
    }

    /**
     * La UI llama a esta función para pedir "volver atrás".
     */
    fun popBackStack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    fun logOut() {
        viewModelScope.launch {
            // 1. Le dice a DataStore que "olvide" la sesión
            sessionPreferencesRepository.setRememberSession(false)

            // 2. Navega a la pantalla de bienvenida
            _navigationEvents.emit(
                NavigationEvent.NavigateTo(
                    route = AppScreens.Welcome,
                    // ¡Importante! Limpiamos la pantalla "Home" del historial
                    // para que el usuario no pueda "volver"
                    popUpToRoute = AppScreens.Home,
                    inclusive = true
                )
            )
        }
    }
}


class MainViewModelFactory(
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(sessionPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}