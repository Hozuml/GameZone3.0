package com.example.gamezone30.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamezone30.data.local.dao.entity.User
import com.example.gamezone30.data.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class RegisterUiState(
    // Campos
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phone: String = "", // Opcional

    // Checkboxes de Géneros
    val availableGenders: List<String> = listOf(
        "Acción", "RPG", "Estrategia", "Deportes", "Aventura", "Simulación", "Terror"
    ),
    val selectedGenders: Set<String> = emptySet(),

    // Errores de validación
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val phoneError: String? = null,
    val genderError: String? = null,
    val generalError: String? = null,

    // Estado de la UI
    val isEmailChecking: Boolean = false,
    val isSubmitting: Boolean = false,
    val registrationSuccess: Boolean = false
)

// El "Cerebro" de la UI de Registro
class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private var emailCheckJob: Job? = null

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name) }
        validateName(name)
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, generalError = null, isEmailChecking = true) } // Ponemos "cargando"

        emailCheckJob?.cancel()
        emailCheckJob = viewModelScope.launch {
            delay(800)
            validateEmail(email, checkAvailability = true)
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
        validatePassword(password)
    }

    fun onConfirmPasswordChange(confirm: String) {
        _uiState.update { it.copy(confirmPassword = confirm) }
        validateConfirmPassword(confirm)
    }

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone) }
        validatePhone(phone)
    }

    fun onGenderToggled(gender: String, isSelected: Boolean) {
        _uiState.update { currentState ->
            val newSelection = currentState.selectedGenders.toMutableSet()
            if (isSelected) {
                newSelection.add(gender)
            } else {
                newSelection.remove(gender)
            }
            currentState.copy(selectedGenders = newSelection, genderError = null)
        }
    }

    fun onSuccessConsumed() {
        _uiState.update { it.copy(registrationSuccess = false) }
    }


    private fun validateName(name: String = _uiState.value.fullName): Boolean {
        val nameRegex = Regex(pattern = "^[a-zA-Z ]+\$")
        val error = when {
            name.isBlank() -> "El nombre no puede estar vacío"
            !name.matches(nameRegex) -> "Solo letras y espacios"
            name.length > 100 -> "Máximo 100 caracteres"
            else -> null
        }
        _uiState.update { it.copy(nameError = error) }
        return error == null
    }


    private suspend fun validateEmail(
        email: String = _uiState.value.email,
        checkAvailability: Boolean = false
    ): Boolean {
        val emailRegex = Regex(pattern = "^[A-Za-z0-9._%+-]+@duoc\\.cl\$") // Formato @duoc.cl
        val error = when {
            email.isBlank() -> "El correo no puede estar vacío"
            !email.matches(emailRegex) -> "Debe ser un correo @duoc.cl"
            email.length > 60 -> "Máximo 60 caracteres"

            checkAvailability && userRepository.isEmailRegistered(email) -> {
                "Este correo ya está registrado"
            }
            else -> null
        }
        _uiState.update { it.copy(emailError = error, isEmailChecking = false) }
        return error == null
    }

    private fun validatePassword(password: String = _uiState.value.password): Boolean {
        val error = when {
            password.length < 10 -> "Al menos 10 caracteres"
            !password.any { it.isUpperCase() } -> "Al menos una mayúscula"
            !password.any { it.isLowerCase() } -> "Al menos una minúscula"
            !password.any { it.isDigit() } -> "Al menos un número"
            !password.any { !it.isLetterOrDigit() } -> "Al menos un carácter especial (@#\$%)"
            else -> null
        }
        _uiState.update { it.copy(passwordError = error) }
        return error == null
    }

    private fun validateConfirmPassword(confirm: String = _uiState.value.confirmPassword): Boolean {
        val error = if (confirm != _uiState.value.password) {
            "Las contraseñas no coinciden"
        } else null
        _uiState.update { it.copy(confirmPasswordError = error) }
        return error == null
    }

    private fun validatePhone(phone: String = _uiState.value.phone): Boolean {
        if (phone.isBlank()) { // Es opcional
            _uiState.update { it.copy(phoneError = null) }
            return true
        }
        val phoneRegex = Regex(pattern = "^[0-9]{8,12}\$")
        val error = if (!phone.matches(phoneRegex)) "Debe ser un número válido" else null
        _uiState.update { it.copy(phoneError = error) }
        return error == null
    }

    private fun validateGenders(): Boolean {
        val error = if (_uiState.value.selectedGenders.isEmpty()) {
            "Debes seleccionar al menos un género"
        } else null
        _uiState.update { it.copy(genderError = error) }
        return error == null
    }

    /**
     * La UI llama a esta función al presionar "Registrar"
     */
    fun onSubmit() {
        // Lanzamos una corutina porque validateEmail AHORA es "suspend"
        viewModelScope.launch {
            // Ejecutamos todas las validaciones al mismo tiempo
            val isNameValid = validateName()
            val isEmailValid = validateEmail(checkAvailability = true) // ¡Validación final con BD!
            val isPasswordValid = validatePassword()
            val isConfirmValid = validateConfirmPassword()
            val isPhoneValid = validatePhone()
            val areGendersValid = validateGenders()

            // Si alguna falla, detenemos
            if (!isNameValid || !isEmailValid || !isPasswordValid || !isConfirmValid || !isPhoneValid || !areGendersValid) {
                return@launch // Salimos de la corutina
            }

            _uiState.update { it.copy(isSubmitting = true) }

            // --- ¡CAMBIO! YA NO SIMULAMOS ---
            try {
                // 1. Creamos el objeto User real
                val newUser = User(
                    fullName = _uiState.value.fullName,
                    email = _uiState.value.email,
                    password = _uiState.value.password, // (Idealmente, encriptar esto)
                    phone = _uiState.value.phone.takeIf { it.isNotBlank() }, // null si está vacío
                    favoriteGenres = _uiState.value.selectedGenders.toList()
                )

                // 2. Llamamos al "Chef" (Repositorio)
                userRepository.registerUser(newUser)

                // 3. ¡Éxito!
                _uiState.update { it.copy(
                    isSubmitting = false,
                    registrationSuccess = true // ¡Bandera de éxito!
                )}
            } catch (e: Exception) {
                // Esto pasará si el email ya existe (por el OnConflictStrategy.ABORT)
                _uiState.update { it.copy(
                    isSubmitting = false,
                    generalError = "Error al registrar: ${e.message}"
                )}
            }
        }
    }
}


class RegisterViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}