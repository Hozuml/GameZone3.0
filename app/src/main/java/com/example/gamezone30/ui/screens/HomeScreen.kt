package com.example.gamezone30.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamezone30.navigation.AppScreens
import com.example.gamezone30.viewmodel.MainViewModel
import com.example.gamezone30.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(

    viewModel: MainViewModel
) {
    // 1. Controla el estado del menú (abierto/cerrado)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // 2. Controla las corutinas para abrir/cerrar el menú
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // 3. Contenido del Menú Lateral
            ModalDrawerSheet {
                Text("GameZone Menú", modifier = Modifier.padding(16.dp))
                Divider()
                NavigationDrawerItem(
                    label = { Text(text = "Mi Perfil") },
                    selected = false,
                    icon = { Icon(Icons.Filled.Person, null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        // Le pide al ViewModel que navegue
                        viewModel.navigateTo(AppScreens.Profile)
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Ajustes") },
                    selected = false,
                    icon = { Icon(Icons.Filled.Settings, null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(AppScreens.Settings)
                    }
                )

                Divider() // Un separador
                NavigationDrawerItem(
                    label = {
                        // Ponemos el texto en color "error" para que destaque
                        Text(text = "Cerrar Sesión", color = MaterialTheme.colorScheme.error)
                    },
                    selected = false,
                    icon = {
                        Icon(
                            Icons.Filled.Logout, // El ícono nuevo
                            null,
                            tint = MaterialTheme.colorScheme.error // Tinte rojo
                        )
                    },
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.logOut()
                    }
                )
            }
        }
    ) {
        // 4. Contenido Principal (Lo que se ve detrás del menú)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Inicio") },
                    // Icono de "hamburguesa" para abrir el menú
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir Menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            // Contenido de tu pantalla
            Column (
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("¡Bienvenido a GameZone!")
            }
        }
    }
}