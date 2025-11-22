package com.example.gamezone30.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamezone30.data.repository.UserRepository
import com.example.gamezone30.data.session.SessionPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI del Login
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

class LoginViewModel(
    private val sessionPreferencesRepository: SessionPreferencesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, generalError = null) }
    }

    fun onRememberSessionChange(isChecked: Boolean) {
        _uiState.update { it.copy(rememberSession = isChecked) }
    }

    // --- LÓGICA NUEVA CON SERVIDOR ---
    fun onSubmit() {
        val state = _uiState.value

        // 1. Validaciones básicas locales (que no estén vacíos)
        val emailError = if (state.email.isBlank()) "Ingresa tu correo" else null
        val passwordError = if (state.password.isBlank()) "Ingresa tu contraseña" else null

        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        // 2. Iniciamos carga
        _uiState.update { it.copy(isSubmitting = true, generalError = null) }

        viewModelScope.launch {
            try {
                // 3. LLAMAMOS AL SERVIDOR (Aquí estaba el error antes)
                // Ahora usamos .login() que devuelve un Result<User>
                val result = userRepository.login(state.email, state.password)

                if (result.isSuccess) {
                    // ¡Login Correcto!
                    val user = result.getOrNull()

                    // Guardamos la sesión localmente (DataStore)
                    sessionPreferencesRepository.saveUserFullName(user?.fullName ?: "Usuario")
                    if (state.rememberSession) {
                        sessionPreferencesRepository.setRememberSession(true)
                    }

                    // Avisamos a la vista que navegue
                    _uiState.update { it.copy(isSubmitting = false, loginSuccess = true) }
                } else {
                    // ¡Error! (Credenciales malas o sin internet)
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                    _uiState.update { it.copy(isSubmitting = false, generalError = errorMsg) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, generalError = "Error desconocido: ${e.message}") }
            }
        }
    }

    fun onLoginSuccessConsumed() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}

// Factory para inyectar repositorios
class LoginViewModelFactory(
    private val sessionPreferencesRepository: SessionPreferencesRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(sessionPreferencesRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}