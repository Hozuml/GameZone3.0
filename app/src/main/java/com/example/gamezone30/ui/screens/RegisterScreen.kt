package com.example.gamezone30.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamezone30.viewmodel.RegisterViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onRegistrationCompleted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // Navegación automática al tener éxito
    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            focusManager.clearFocus(force = true)
            delay(1600) // Muestra el mensaje de éxito por un momento
            viewModel.onSuccessConsumed()
            onRegistrationCompleted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // ========================================================
            // ¡¡AQUÍ ESTÁ EL CÓDIGO QUE FALTABA!!
            // ========================================================
            AnimatedVisibility(
                visible = uiState.registrationSuccess,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 })
            ) {
                // Usamos una Columna para que el Card y el Spacer se apilen
                Column {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "¡Registro exitoso!",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Estamos guardando tus datos...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            // ========================================================
            // FIN DEL CÓDIGO NUEVO
            // ========================================================


            Text(
                text = "Completa tus datos para comenzar",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Nombre Completo
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.fullName,
                onValueChange = viewModel::onFullNameChange,
                label = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Filled.Person, null) },
                isError = uiState.nameError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            FieldErrorMessage(uiState.nameError)
            Spacer(modifier = Modifier.height(12.dp))

            // Correo Electrónico
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Correo institucional (@duoc.cl)") },
                leadingIcon = { Icon(Icons.Filled.Email, null) },
                isError = uiState.emailError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email)
            )
            FieldErrorMessage(uiState.emailError)
            Spacer(modifier = Modifier.height(12.dp))

            // Contraseña
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, null) },
                supportingText = { Text("10+ caracteres, 1 mayús, 1 minús, 1 núm, 1 especial") },
                isError = uiState.passwordError != null,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password)
            )
            FieldErrorMessage(uiState.passwordError)
            Spacer(modifier = Modifier.height(12.dp))

            // Confirmar Contraseña
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = { Text("Confirmar contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, null) },
                isError = uiState.confirmPasswordError != null,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password)
            )
            FieldErrorMessage(uiState.confirmPasswordError)
            Spacer(modifier = Modifier.height(12.dp))

            // Teléfono (Opcional)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Teléfono (opcional)") },
                leadingIcon = { Icon(Icons.Filled.Phone, null) },
                isError = uiState.phoneError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Phone)
            )
            FieldErrorMessage(uiState.phoneError)
            Spacer(modifier = Modifier.height(16.dp))

            // Géneros Favoritos (Checklist)
            Text("Selecciona tus géneros de interés", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                uiState.availableGenders.forEach { gender ->
                    val isSelected = uiState.selectedGenders.contains(gender)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                viewModel.onGenderToggled(gender, checked)
                            }
                        )
                        Text(text = gender, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            FieldErrorMessage(uiState.genderError)
            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Registro
            Button(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                onClick = {
                    focusManager.clearFocus(force = true)
                    viewModel.onSubmit()
                },
                enabled = !uiState.isSubmitting
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Crear cuenta", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


// (La función de ayuda de error es la misma que te di para el Login)
@Composable
private fun FieldErrorMessage(errorMessage: String?) {
    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = errorMessage,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Start
        )
    }
}