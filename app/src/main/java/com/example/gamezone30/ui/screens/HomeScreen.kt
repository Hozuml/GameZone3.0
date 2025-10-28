package com.example.gamezone30.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gamezone30.R
import com.example.gamezone30.navigation.AppScreens
import com.example.gamezone30.ui.theme.DarkBackgroundColor
import com.example.gamezone30.ui.theme.LightTextColor
import com.example.gamezone30.ui.theme.SecondaryTextColor
import com.example.gamezone30.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userName by viewModel.userFullName.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("GameZone Menú", modifier = Modifier.padding(16.dp), color = LightTextColor)
                HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.5f))
                NavigationDrawerItem(
                    label = { Text(text = "Mi Perfil") },
                    selected = false,
                    icon = { Icon(Icons.Filled.Person, null, tint = LightTextColor) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(AppScreens.Profile)
                    }
                )
                HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.5f))
                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = MaterialTheme.colorScheme.error) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.logOut()
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = DarkBackgroundColor,
            topBar = {
                TopAppBar(
                    title = { Text("Inicio", color = LightTextColor) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir Menú", tint = LightTextColor)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.navigateTo(AppScreens.Profile) }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Ajustes", tint = LightTextColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackgroundColor
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("¡Bienvenido a GameZone!", style = MaterialTheme.typography.headlineMedium, color = LightTextColor)
                Spacer(modifier = Modifier.height(24.dp))
                UserProfileCard(
                    userName = userName ?: "Usuario",
                    userLocation = "Santiago, Chile",
                    onEditPhoto = { viewModel.navigateTo(AppScreens.Profile) }
                )
            }
        }
    }
}

@Composable
fun UserProfileCard(userName: String, userLocation: String, onEditPhoto: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E38)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_app),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Column(modifier = Modifier.weight(1.0f)) {
                Text(userName, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = LightTextColor)
                Text(userLocation, style = MaterialTheme.typography.bodyMedium, color = SecondaryTextColor)
            }
            IconButton(onClick = onEditPhoto) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar foto", tint = LightTextColor)
            }
        }
    }
}
