package com.example.gamezone30.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.gamezone30.R
import com.example.gamezone30.ui.theme.DarkBackgroundColor
import com.example.gamezone30.ui.theme.LightTextColor
import com.example.gamezone30.ui.theme.PrimaryColor
import com.example.gamezone30.ui.theme.SecondaryTextColor
import com.example.gamezone30.viewmodel.ProfileViewModel
import com.example.gamezone30.viewmodel.SharedViewModel
import com.google.android.gms.location.LocationServices
import java.io.File
import java.util.UUID
import androidx.core.content.FileProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    sharedViewModel: SharedViewModel,
    onNavigateBack: () -> Unit,
    onTakePhoto: (Uri) -> Unit,
    onSelectImage: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val imageUri by sharedViewModel.imageUri.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) { isGranted ->
            if (isGranted) {
                @SuppressLint("MissingPermission")
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        viewModel.getAddressFromCoordinates(context, it.latitude, it.longitude)
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cambiar foto de perfil") },
            text = { Text("¿Cómo quieres actualizar tu foto de perfil?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val file = File(context.cacheDir, "${UUID.randomUUID()}.jpg")
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        sharedViewModel.setImageUri(uri)
                        onTakePhoto(uri)
                        showDialog = false
                    }
                ) {
                    Text("Tomar foto")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onSelectImage()
                        showDialog = false
                    }
                ) {
                    Text("Galería")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", color = LightTextColor) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = LightTextColor)
                    }
                },
                actions = {
                    if (uiState.isEditing) {
                        TextButton(onClick = { viewModel.onSaveChanges() }) {
                            Text("Guardar", color = PrimaryColor)
                        }
                        TextButton(onClick = { viewModel.onEditToggled() }) {
                            Text("Cancelar", color = SecondaryTextColor)
                        }
                    } else {
                        IconButton(onClick = { viewModel.onEditToggled() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = LightTextColor)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackgroundColor
                )
            )
        },
        containerColor = DarkBackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = if (imageUri != null) rememberAsyncImagePainter(imageUri) else painterResource(id = R.drawable.home_app),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { showDialog = true },
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.isEditing) {
                OutlinedTextField(
                    value = uiState.fullName,
                    onValueChange = { viewModel.onFullNameChanged(it) },
                    label = { Text("Nombre Completo") }
                )
            } else {
                Text(
                    text = uiState.fullName,
                    color = LightTextColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = uiState.location.ifEmpty { "Ubicación desconocida" },
                color = SecondaryTextColor,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E38))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Detalles de la Cuenta", color = LightTextColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileInfoRow(icon = Icons.Default.Person, label = "Nombre Completo", value = uiState.fullName, isEditing = uiState.isEditing, onValueChange = { viewModel.onFullNameChanged(it) })
                    HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                    ProfileInfoRow(icon = Icons.Default.Email, label = "Correo Electrónico", value = uiState.email, isEditing = false, onValueChange = {})
                    HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                    ProfileInfoRow(icon = Icons.Default.Phone, label = "Teléfono", value = uiState.phone, isEditing = uiState.isEditing, onValueChange = { viewModel.onPhoneChanged(it) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E38))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Preferencias de Juego", color = LightTextColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileInfoRow(icon = Icons.Default.Games, label = "Géneros Favoritos", value = uiState.favoriteGenres.joinToString(", "), isEditing = false, onValueChange = {})
                    HorizontalDivider(color = SecondaryTextColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                    ProfileInfoRow(icon = Icons.Default.Link, label = "Cuentas Vinculadas", value = "PSN, Steam", isEditing = false, onValueChange = {})
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Actualizar Ubicación", color = DarkBackgroundColor, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = { /* TODO */ }) {
                Text("Cerrar Sesión", color = Color.Red)
            }
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    label: String, 
    value: String, 
    isEditing: Boolean, 
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, color = SecondaryTextColor, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        if (isEditing && (label == "Nombre Completo" || label == "Teléfono")) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = LightTextColor,
                    unfocusedTextColor = LightTextColor,
                    cursorColor = PrimaryColor,
                    focusedContainerColor = Color.Transparent, 
                    unfocusedContainerColor = Color.Transparent, 
                    focusedIndicatorColor = PrimaryColor,
                    unfocusedIndicatorColor = SecondaryTextColor.copy(alpha = 0.5f)
                )
            )
        } else {
            Text(value, color = LightTextColor, fontSize = 16.sp)
        }
    }
}