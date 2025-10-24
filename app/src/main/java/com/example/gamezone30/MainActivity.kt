package com.example.gamezone30

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gamezone30.data.local.dao.database.AppDatabase
import com.example.gamezone30.data.repository.UserRepository
import com.example.gamezone30.data.session.SessionPreferencesRepository
import com.example.gamezone30.data.session.sessionDataStore
import com.example.gamezone30.navigation.AppScreens
import com.example.gamezone30.navigation.NavigationEvent
import com.example.gamezone30.ui.screens.*
import com.example.gamezone30.ui.theme.GameZone30Theme
import com.example.gamezone30.viewmodel.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {

    private val sessionPreferencesRepository by lazy {
        SessionPreferencesRepository(applicationContext.sessionDataStore)
    }

    private val database by lazy { AppDatabase.getInstance(applicationContext) }
    private val userRepository by lazy { UserRepository(database.userDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
                enableEdgeToEdge()
        setContent {
            GameZone30Theme {
                val navController = rememberNavController()


                val mainViewModel: MainViewModel = viewModel(
                    factory = remember {
                        MainViewModelFactory(sessionPreferencesRepository)
                    }
                )

                // 4. Lógica para "Recordar Sesión"
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    val rememberSession = sessionPreferencesRepository.rememberSessionFlow.first()
                    startDestination = if (rememberSession) {
                        AppScreens.Home.route // Si sí, va directo a Home
                    } else {
                        AppScreens.Welcome.route // Si no, va a la Bienvenida
                    }
                }

                // 5. "Escucha" los eventos de navegación del MainViewModel
                LaunchedEffect(mainViewModel, navController) {
                    mainViewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(event.route.route) {
                                    event.popUpToRoute?.let { target ->
                                        popUpTo(target.route) { inclusive = event.inclusive }
                                    }
                                    launchSingleTop = event.singleTop
                                }
                            }
                            NavigationEvent.PopBackStack -> navController.popBackStack()
                            NavigationEvent.NavigateUp -> navController.navigateUp()
                        }
                    }
                }

                // 6. Muestra un "Cargando" mientras revisa la sesión
                if (startDestination == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // 7. ¡El NAVEGADOR! Aquí se definen todas las rutas
                    NavHost(
                        navController = navController,
                        startDestination = startDestination!! // Inicia en la ruta que decidimos
                    ) {
                        // Pantalla de Bienvenida
                        composable(AppScreens.Welcome.route) {
                            WelcomeScreen(
                                onLoginClick = { navController.navigate(AppScreens.Login.route) },
                                onRegisterClick = { navController.navigate(AppScreens.Register.route) }
                            )
                        }

                        // Pantalla de Login
                        composable(AppScreens.Login.route) {
                            // ¡Usamos la "Fábrica" para pasarle los repositorios!
                            val loginViewModel: LoginViewModel = viewModel(
                                factory = remember {
                                    LoginViewModelFactory(sessionPreferencesRepository, userRepository)
                                }
                            )
                            LoginScreen(
                                viewModel = loginViewModel,
                                onNavigateToHome = { rememberSession ->
                                    navController.navigate(AppScreens.Home.route) {
                                        // Borra la pila de navegación para que no pueda "volver"
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate(AppScreens.Register.route)
                                }
                            )
                        }

                        // Pantalla de Registro
                        composable(AppScreens.Register.route) {
                            // ¡Usamos la "Fábrica" para pasarle el repositorio!
                            val registerViewModel: RegisterViewModel = viewModel(
                                factory = remember {
                                    RegisterViewModelFactory(userRepository)
                                }
                            )
                            RegisterScreen(
                                viewModel = registerViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onRegistrationCompleted = {
                                    // Vuelve al Login después de registrarse
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Pantalla de Home
                        composable(AppScreens.Home.route) {
                            // Le pasamos el MainViewModel para que pueda navegar
                            HomeScreen(viewModel = mainViewModel)
                        }

                        // Pantallas del menú lateral (de ejemplo)
                        composable(AppScreens.Profile.route) {
                            // TODO: Crear Pantalla de Perfil
                        }
                        composable(AppScreens.Settings.route) {
                            // TODO: Crear Pantalla de Ajustes
                        }
                    }
                }
            }
        }
    }
}