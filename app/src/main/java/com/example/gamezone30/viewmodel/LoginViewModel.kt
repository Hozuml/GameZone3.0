package com.example.gamezone30.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamezone30.data.repository.UserRepository
import com.example.gamezone30.data.session.SessionPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, generalError = null) }
    }

    fun onRememberSessionChange(remember: Boolean) {
        _uiState.update { it.copy(rememberSession = remember) }
    }

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
            val user = userRepository.findUserByEmail(state.email)
            val loginSuccessful = user != null && user.password == state.password

            if (loginSuccessful) {
                sessionPreferencesRepository.setRememberSession(state.rememberSession)
                sessionPreferencesRepository.saveUserFullName(user!!.fullName)
                _uiState.update { it.copy(isSubmitting = false, loginSuccess = true) }
            } else {
                _uiState.update { it.copy(isSubmitting = false, generalError = "Correo o contraseña inválidos") }
            }
        }
    }

    fun onLoginSuccessConsumed() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}

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