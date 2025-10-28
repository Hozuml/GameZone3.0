package com.example.gamezone30.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamezone30.ui.theme.*
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
        containerColor = DarkBackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta", color = LightTextColor) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, "Volver", tint = LightTextColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackgroundColor
                )
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
            AnimatedVisibility(
                visible = uiState.registrationSuccess,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 })
            ) {
                Column {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = PrimaryColor,
                            contentColor = DarkBackgroundColor
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
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
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

            Text(
                text = "Completa tus datos para comenzar",
                color = LightTextColor,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Nombre Completo
            CustomOutlinedTextField(
                value = uiState.fullName,
                onValueChange = viewModel::onFullNameChange,
                label = "Nombre completo",
                leadingIcon = Icons.Filled.Person,
                isError = uiState.nameError != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            FieldErrorMessage(uiState.nameError)
            Spacer(modifier = Modifier.height(12.dp))

            // Correo Electrónico
            CustomOutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = "Correo institucional (@duoc.cl)",
                leadingIcon = Icons.Filled.Email,
                isError = uiState.emailError != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email)
            )
            FieldErrorMessage(uiState.emailError)
            Spacer(modifier = Modifier.height(12.dp))

            // Contraseña
            CustomOutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Contraseña",
                leadingIcon = Icons.Filled.Lock,
                isError = uiState.passwordError != null,
                supportingText = "10+ caracteres, 1 mayús, 1 minús, 1 núm, 1 especial",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password)
            )
            FieldErrorMessage(uiState.passwordError)
            Spacer(modifier = Modifier.height(12.dp))

            // Confirmar Contraseña
            CustomOutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = "Confirmar contraseña",
                leadingIcon = Icons.Filled.Lock,
                isError = uiState.confirmPasswordError != null,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password)
            )
            FieldErrorMessage(uiState.confirmPasswordError)
            Spacer(modifier = Modifier.height(12.dp))

            // Teléfono (Opcional)
            CustomOutlinedTextField(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange,
                label = "Teléfono (opcional)",
                leadingIcon = Icons.Filled.Phone,
                isError = uiState.phoneError != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Phone)
            )
            FieldErrorMessage(uiState.phoneError)
            Spacer(modifier = Modifier.height(16.dp))

            // Géneros Favoritos (Checklist)
            Text("Selecciona tus géneros de interés", style = MaterialTheme.typography.titleMedium, color = LightTextColor)
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
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryColor,
                                uncheckedColor = SecondaryTextColor
                            )
                        )
                        Text(text = gender, style = MaterialTheme.typography.bodyMedium, color = LightTextColor)
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = DarkBackgroundColor
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isSubmitting
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DarkBackgroundColor)
                } else {
                    Text("Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isError: Boolean,
    keyboardOptions: KeyboardOptions,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    supportingText: String? = null
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = SecondaryTextColor) },
        leadingIcon = { Icon(leadingIcon, null, tint = SecondaryTextColor) },
        isError = isError,
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = LightTextColor,
            unfocusedTextColor = LightTextColor,
            cursorColor = PrimaryColor,
            focusedIndicatorColor = PrimaryColor,
            unfocusedIndicatorColor = SecondaryTextColor.copy(alpha = 0.5f),
            focusedContainerColor = TextFieldBackgroundColor,
            unfocusedContainerColor = TextFieldBackgroundColor,
            disabledContainerColor = TextFieldBackgroundColor,
            focusedLabelColor = SecondaryTextColor,
            unfocusedLabelColor = SecondaryTextColor,
            focusedLeadingIconColor = SecondaryTextColor,
            unfocusedLeadingIconColor = SecondaryTextColor
        )

    )
    if (supportingText != null) {
        Text(
            text = supportingText,
            color = SecondaryTextColor.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
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
