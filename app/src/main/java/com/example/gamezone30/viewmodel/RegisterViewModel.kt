package com.example.gamezone30.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamezone30.data.local.dao.entity.User
import com.example.gamezone30.data.repository.UserRepository
import com.example.gamezone30.data.session.SessionPreferencesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// El estado de la UI se mantiene igual, ¡excelente estructura!
data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phone: String = "",
    val availableGenders: List<String> = listOf(
        "Acción", "RPG", "Estrategia", "Deportes", "Aventura", "Simulación", "Terror"
    ),
    val selectedGenders: Set<String> = emptySet(),
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val phoneError: String? = null,
    val genderError: String? = null,
    val generalError: String? = null,
    val isEmailChecking: Boolean = false, // Esto ahora será visual solamente
    val isSubmitting: Boolean = false,
    val registrationSuccess: Boolean = false
)

class RegisterViewModel(
    private val userRepository: UserRepository,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private var emailCheckJob: Job? = null

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name) }
        validateName(name)
    }

    fun onEmailChange(email: String) {
        // Quitamos el "isEmailChecking = true" porque ya no consultamos a la BD local
        _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
        emailCheckJob?.cancel()
        emailCheckJob = viewModelScope.launch {
            // Mantenemos el delay para validar el REGEX sin molestar al usuario mientras escribe
            delay(800)
            validateEmail(email)
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
            if (isSelected) newSelection.add(gender) else newSelection.remove(gender)
            currentState.copy(selectedGenders = newSelection, genderError = null)
        }
    }

    fun onSuccessConsumed() {
        _uiState.update { it.copy(registrationSuccess = false) }
    }

    // --- VALIDACIONES LOCALES ---

    private fun validateName(name: String = _uiState.value.fullName): Boolean {
        val nameRegex = Regex("^[a-zA-Z ]+$")
        val error = when {
            name.isBlank() -> "El nombre no puede estar vacío"
            !name.matches(nameRegex) -> "Solo letras y espacios"
            name.length > 100 -> "Máximo 100 caracteres"
            else -> null
        }
        _uiState.update { it.copy(nameError = error) }
        return error == null
    }

    // CAMBIO IMPORTANTE: Quitamos 'checkAvailability'.
    // Ahora solo validamos formato @duoc.cl. La unicidad la valida el servidor al enviar.
    private fun validateEmail(email: String = _uiState.value.email): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@duoc\\.cl$")
        val error = when {
            email.isBlank() -> "El correo no puede estar vacío"
            !email.matches(emailRegex) -> "Debe ser un correo @duoc.cl"
            email.length > 60 -> "Máximo 60 caracteres"
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
        val error = if (confirm != _uiState.value.password) "Las contraseñas no coinciden" else null
        _uiState.update { it.copy(confirmPasswordError = error) }
        return error == null
    }

    private fun validatePhone(phone: String = _uiState.value.phone): Boolean {
        if (phone.isBlank()) {
            _uiState.update { it.copy(phoneError = null) }
            return true
        }
        val phoneRegex = Regex("^[0-9]{8,12}$")
        val error = if (!phone.matches(phoneRegex)) "Debe ser un número válido" else null
        _uiState.update { it.copy(phoneError = error) }
        return error == null
    }

    private fun validateGenders(): Boolean {
        val error = if (_uiState.value.selectedGenders.isEmpty()) "Debes seleccionar al menos un género" else null
        _uiState.update { it.copy(genderError = error) }
        return error == null
    }

    // --- ENVÍO AL SERVIDOR (CAMBIO PRINCIPAL) ---

    fun onSubmit() {
        println("DEBUG: 1. Se presionó el botón Enviar") // <--- Chismoso 1

        viewModelScope.launch {
            // 1. Validaciones locales
            val isNameValid = validateName()
            val isEmailValid = validateEmail()
            val isPasswordValid = validatePassword()
            val isConfirmValid = validateConfirmPassword()
            val isPhoneValid = validatePhone()
            val areGendersValid = validateGenders()

            if (!isNameValid || !isEmailValid || !isPasswordValid || !isConfirmValid || !isPhoneValid || !areGendersValid) {
                println("DEBUG: 2. Falló alguna validación local") // <--- Chismoso 2
                // Imprimimos qué falló para saber
                if (!isNameValid) println("DEBUG: Falló Nombre")
                if (!isEmailValid) println("DEBUG: Falló Email")
                if (!isPasswordValid) println("DEBUG: Falló Password")
                if (!isConfirmValid) println("DEBUG: Falló Confirmar Password")
                if (!isPhoneValid) println("DEBUG: Falló Teléfono")
                if (!areGendersValid) println("DEBUG: Falló Géneros")
                return@launch
            }

            println("DEBUG: 3. Validaciones OK. Preparando envío...") // <--- Chismoso 3
            _uiState.update { it.copy(isSubmitting = true, generalError = null) }

            val newUser = User(
                email = _uiState.value.email,
                fullName = _uiState.value.fullName,
                password = _uiState.value.password,
                phone = _uiState.value.phone.takeIf { it.isNotBlank() },
                favoriteGenres = _uiState.value.selectedGenders.toList()
            )

            println("DEBUG: 4. Llamando al repositorio...") // <--- Chismoso 4
            val result = userRepository.registerUser(newUser)

            if (result.isSuccess) {
                println("DEBUG: 5. ¡ÉXITO! El servidor respondió bien") // <--- Chismoso 5
                sessionPreferencesRepository.saveUserFullName(_uiState.value.fullName)
                _uiState.update { it.copy(isSubmitting = false, registrationSuccess = true) }
            } else {
                val mensajeError = result.exceptionOrNull()?.message ?: "Error desconocido"
                println("DEBUG: 6. ERROR DEL SERVIDOR: $mensajeError") // <--- Chismoso 6
                _uiState.update { it.copy(isSubmitting = false, generalError = mensajeError) }
            }
        }
    }
}
class RegisterViewModelFactory(
    private val userRepository: UserRepository,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(userRepository, sessionPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}