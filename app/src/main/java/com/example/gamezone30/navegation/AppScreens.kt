package com.example.gamezone30.navigation

// Define todas las rutas (pantallas) de nuestra aplicación
sealed class AppScreens(val route: String) {
    object Welcome : AppScreens("welcome_screen") // Tu pantalla de bienvenida
    object Login : AppScreens("login_screen")
    object Register : AppScreens("register_screen")
    object Home : AppScreens("home_screen")
    object Profile : AppScreens("profile_screen") // Para el menú lateral
    object Settings : AppScreens("settings_screen") // Para el menú lateral
}

sealed class NavigationEvent {
    // Evento para navegar a una ruta específica
    data class NavigateTo(
        val route: AppScreens,
        val popUpToRoute: AppScreens? = null, // Para "limpiar" pantallas anteriores
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    // Evento para simplemente "volver atrás"
    object PopBackStack : NavigationEvent()

    // Evento para "subir" (similar a atrás)
    object NavigateUp : NavigationEvent()
}