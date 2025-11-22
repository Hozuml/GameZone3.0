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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.gamezone30.data.local.dao.entity.Game
import com.example.gamezone30.navigation.AppScreens
import com.example.gamezone30.ui.theme.DarkBackgroundColor
import com.example.gamezone30.ui.theme.LightTextColor
import com.example.gamezone30.ui.theme.SecondaryTextColor
import com.example.gamezone30.viewmodel.HomeViewModel
import com.example.gamezone30.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    mainViewModel: MainViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val userName by mainViewModel.userFullName.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet(
                drawerContainerColor = DarkBackgroundColor
            ) {
                Column {
                    Text("GameZone Menú", modifier = Modifier.padding(16.dp), color = LightTextColor)
                    HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.5f))

                    // Item 1: Perfil
                    NavigationDrawerItem(
                        label = { Text(text = "Mi Perfil", color = LightTextColor) },
                        selected = false,
                        icon = { Icon(Icons.Filled.Person, null, tint = LightTextColor) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            mainViewModel.navigateTo(AppScreens.Profile)
                        },
                        colors = androidx.compose.material3.NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = DarkBackgroundColor,
                            selectedContainerColor = Color(0xFF2E2E48)
                        )
                    )

                    HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.5f))

                    // Item 2: Cerrar Sesión
                    NavigationDrawerItem(
                        label = { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error) },
                        selected = false,
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = MaterialTheme.colorScheme.error) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            mainViewModel.logOut()
                        },
                        colors = androidx.compose.material3.NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = DarkBackgroundColor,
                            selectedContainerColor = Color(0xFF2E2E48)
                        )
                    )
                }
            }
        }
    ) {
        Scaffold(
            containerColor = DarkBackgroundColor,
            topBar = {
                TopAppBar(
                    title = { Text("Catálogo (${uiState.localGameList.size} juegos)", color = LightTextColor) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir Menú", tint = LightTextColor)
                        }
                    },
                    actions = {
                        IconButton(onClick = { mainViewModel.navigateTo(AppScreens.Profile) }) {
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
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserProfileCard(
                    userName = userName ?: "Usuario Invitado",

                    userLocation = uiState.weatherInfo,
                    onEditPhoto = { mainViewModel.navigateTo(AppScreens.Profile) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- SECCIÓN DE ERRORES Y CARGA ---
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.Cyan, modifier = Modifier.padding(top = 40.dp))
                    Text("Cargando catálogo...", color = SecondaryTextColor, modifier = Modifier.padding(top = 16.dp))
                } else if (uiState.errorMessage != null) {
                    Text("ERROR: ${uiState.errorMessage}", color = Color.Red, fontSize = 18.sp, modifier = Modifier.padding(top = 24.dp))
                    Text("Revisa el servidor Spring Boot.", color = SecondaryTextColor)
                } else {
                    // 1. Catálogo Local (Tu Microservicio - Punto I)
                    SectionHeader(title = "Tu Catálogo (Microservicio Propio)", count = uiState.localGameList.size)
                    GameList(games = uiState.localGameList)

                    Spacer(modifier = Modifier.height(24.dp))

                }
            }
        }
    }
}

// Composables de apoyo que se agregaron para mostrar la lista:

@Composable
fun SectionHeader(title: String, count: Int, color: Color = LightTextColor) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontWeight = FontWeight.Bold, color = color, fontSize = 16.sp)
        Text("Total: $count", color = SecondaryTextColor, fontSize = 14.sp)
    }
    HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.5f))
}


@Composable
fun GameList(games: List<Game>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(games) { game ->
            GameItemCard(game = game)
        }
    }
    if (games.isEmpty()) {
        Text("No hay juegos que mostrar.", color = SecondaryTextColor.copy(alpha = 0.7f), modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun GameItemCard(game: Game) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E48)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(game.title, fontWeight = FontWeight.Bold, color = LightTextColor, fontSize = 18.sp)
                Text("Género: ${game.genre}", style = MaterialTheme.typography.bodyMedium, color = SecondaryTextColor)
                Text(game.description ?: "Sin descripción.", style = MaterialTheme.typography.bodySmall, color = SecondaryTextColor.copy(alpha = 0.7f), maxLines = 2)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("$${game.price}", fontWeight = FontWeight.ExtraBold, color = Color.Cyan, fontSize = 22.sp)
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
            // (Asumimos que R.drawable.home_app existe)
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