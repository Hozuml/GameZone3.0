package com.example.gamezone30.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
// ¡CAMBIO! Importamos el Repositorio de Usuario
import com.example.gamezone30.data.repository.UserRepository
import com.example.gamezone30.data.session.SessionPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// El "Estado" de la UI de Login (sigue igual)
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val isSubmitting: Boolean = false,
    val loginSuccess: Boolean = false,
    val rememberSession: Boolean = false
)

// El "Cerebro" de la UI de Login
class LoginViewModel(
    private val sessionPreferencesRepository: SessionPreferencesRepository,
    // ¡CAMBIO! Ahora pedimos también el UserRepository
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // (onEmailChange, onPasswordChange, onRememberSessionChange...
    // ...siguen exactamente iguales que antes, no hace falta repetirlos)

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, generalError = null) }
    }

    fun onRememberSessionChange(remember: Boolean) {
        _uiState.update { it.copy(rememberSession = remember) }
    }


    /**
     * ¡CAMBIO! La lógica de Submit ahora es real.
     */
    fun onSubmit() {
        val state = _uiState.value
        val emailError = if (state.email.isBlank()) "El correo no puede estar vacío" else null
        val passwordError = if (state.password.isBlank()) "La contraseña no puede estar vacía" else null

        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {

            // --- ¡CAMBIO! YA NO SIMULAMOS ---
            // 1. Buscamos al usuario en la BD
            val user = userRepository.findUserByEmail(state.email)

            // 2. Verificamos
            val loginExitoso = (user != null && user.password == state.password)
            // ---------------------------------

            if (loginExitoso) {
                // ¡ÉXITO!
                if (state.rememberSession) {
                    sessionPreferencesRepository.setRememberSession(true)
                }
                _uiState.update { it.copy(isSubmitting = false, loginSuccess = true) }
            } else {
                // ¡FALLO!
                _uiState.update { it.copy(
                    isSubmitting = false,
                    generalError = "Correo o contraseña inválidos" // Mensaje de error del PDF [cite: 32]
                )}
            }
        }
    }

    fun onLoginSuccessConsumed() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}

// ¡CAMBIO! La "Fábrica" ahora pide los dos repositorios
class LoginViewModelFactory(
    private val sessionPreferencesRepository: SessionPreferencesRepository,
    private val userRepository: UserRepository // ¡CAMBIO!
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // ¡CAMBIO! Le pasamos ambos al ViewModel
            return LoginViewModel(sessionPreferencesRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}