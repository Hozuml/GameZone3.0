package com.example.gamezone30.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamezone30.data.session.SessionPreferencesRepository
import com.example.gamezone30.navigation.AppScreens
import com.example.gamezone30.navigation.NavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    val userFullName: StateFlow<String?> = sessionPreferencesRepository.userFullNameFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

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

    fun popBackStack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    fun logOut() {
        viewModelScope.launch {
            sessionPreferencesRepository.clearSession()
            _navigationEvents.emit(
                NavigationEvent.NavigateTo(
                    route = AppScreens.Welcome,
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