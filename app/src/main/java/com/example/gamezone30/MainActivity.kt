package com.example.gamezone30

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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

    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var selectImageLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                sharedViewModel.setImageUri(sharedViewModel.imageUri.value)
            }
        }

        selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                sharedViewModel.setImageUri(it)
            }
        }

        setContent {
            GameZone30Theme {
                val navController = rememberNavController()

                val mainViewModel: MainViewModel = viewModel(
                    factory = remember {
                        MainViewModelFactory(sessionPreferencesRepository)
                    }
                )

                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    val rememberSession = sessionPreferencesRepository.rememberSessionFlow.first()
                    startDestination = if (rememberSession) {
                        AppScreens.Home.route
                    } else {
                        AppScreens.Welcome.route
                    }
                }

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

                if (startDestination == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination!!
                    ) {
                        composable(AppScreens.Welcome.route) {
                            WelcomeScreen(
                                onLoginClick = { navController.navigate(AppScreens.Login.route) },
                                onRegisterClick = { navController.navigate(AppScreens.Register.route) }
                            )
                        }

                        composable(AppScreens.Login.route) {
                            val loginViewModel: LoginViewModel = viewModel(
                                factory = remember {
                                    LoginViewModelFactory(sessionPreferencesRepository, userRepository)
                                }
                            )
                            LoginScreen(
                                viewModel = loginViewModel,
                                onNavigateToHome = { rememberSession ->
                                    navController.navigate(AppScreens.Home.route) {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate(AppScreens.Register.route)
                                }
                            )
                        }

                        composable(AppScreens.Register.route) {
                            val registerViewModel: RegisterViewModel = viewModel(
                                factory = remember {
                                    RegisterViewModelFactory(userRepository, sessionPreferencesRepository)
                                }
                            )
                            RegisterScreen(
                                viewModel = registerViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onRegistrationCompleted = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(AppScreens.Home.route) {
                            HomeScreen(viewModel = mainViewModel)
                        }

                        composable(AppScreens.Profile.route) {
                            val profileViewModel: ProfileViewModel = viewModel(
                                factory = remember {
                                    ProfileViewModelFactory(userRepository, sessionPreferencesRepository)
                                }
                            )
                            ProfileScreen(
                                viewModel = profileViewModel,
                                sharedViewModel = sharedViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onTakePhoto = { uri -> takePictureLauncher.launch(uri) },
                                onSelectImage = { selectImageLauncher.launch("image/*") }
                            )
                        }
                    }
                }
            }
        }
    }
}