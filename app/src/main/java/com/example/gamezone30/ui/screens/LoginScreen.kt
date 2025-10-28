package com.example.gamezone30.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gamezone30.ui.theme.DarkBackgroundColor
import com.example.gamezone30.ui.theme.LightTextColor
import com.example.gamezone30.ui.theme.PrimaryColor
import com.example.gamezone30.ui.theme.SecondaryTextColor
import com.example.gamezone30.ui.theme.TextFieldBackgroundColor
import com.example.gamezone30.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToHome: (Boolean) -> Unit, // Callback para navegar
    onNavigateToRegister: () -> Unit     // Callback para navegar
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // Navegación automática al tener éxito
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            focusManager.clearFocus()
            onNavigateToHome(uiState.rememberSession)
            viewModel.onLoginSuccessConsumed()
        }
    }

    Scaffold(containerColor = DarkBackgroundColor) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // Títulos
            Text("GameZone", color = LightTextColor, fontSize = 48.sp, fontWeight = FontWeight.Bold)
            Text("¡Bienvenido de vuelta!", color = LightTextColor, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(48.dp))

            // Campo de Email
            Text("Correo electrónico", color = LightTextColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("tu.email@duoc.cl", color = SecondaryTextColor) }, // ¡Pista clave!
                leadingIcon = { Icon(Icons.Filled.Email, null, tint = SecondaryTextColor) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                isError = uiState.emailError != null,
                colors = getTextFieldColors(TextFieldBackgroundColor, LightTextColor) // Colores personalizados
            )
            FieldErrorMessage(uiState.emailError)
            Spacer(modifier = Modifier.height(24.dp))

            // Campo de Contraseña
            Text("Contraseña", color = LightTextColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Introduce tu contraseña", color = SecondaryTextColor) },
                leadingIcon = { Icon(Icons.Filled.Lock, null, tint = SecondaryTextColor) },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, "Mostrar/Ocultar")
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                isError = uiState.passwordError != null,
                colors = getTextFieldColors(TextFieldBackgroundColor, LightTextColor)
            )
            FieldErrorMessage(uiState.passwordError)

            // Checkbox y "Olvidaste" (Requisito del PDF )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.rememberSession,
                        onCheckedChange = viewModel::onRememberSessionChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryColor,
                            uncheckedColor = SecondaryTextColor
                        )
                    )
                    Text(text = "Recordar sesión", color = LightTextColor)
                }
                TextButton(onClick = { /* TODO */ }) {
                    Text("¿Olvidaste?", color = PrimaryColor, textAlign = TextAlign.End)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Iniciar Sesión
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.onSubmit()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
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
                    Text("Iniciar sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            FieldErrorMessage(uiState.generalError)
            Spacer(modifier = Modifier.height(32.dp))

            // Link de Registro
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿No tienes una cuenta? ", color = LightTextColor)
                TextButton(onClick = onNavigateToRegister) {
                    Text("Regístrate", color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

// Función de ayuda para no repetir los colores del TextField
@Composable
private fun getTextFieldColors(
    background: Color,
    textColor: Color
): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedContainerColor = background,
        unfocusedContainerColor = background,
        disabledContainerColor = background,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        errorContainerColor = background
    )
}

// Función de ayuda para mostrar errores
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