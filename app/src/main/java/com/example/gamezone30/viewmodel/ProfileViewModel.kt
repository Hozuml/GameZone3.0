package com.example.gamezone30.viewmodel

import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamezone30.data.repository.UserRepository
import com.example.gamezone30.data.session.SessionPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class ProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    // CAMBIO 1: Ahora se llama 'location' para que coincida con tu pantalla
    val location: String = "",
    // CAMBIO 2: Agregamos la lista de géneros que faltaba
    val favoriteGenres: List<String> = emptyList(),
    val isEditing: Boolean = false
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionPreferencesRepository.userFullNameFlow.collectLatest { name ->
                // Usamos el operador Elvis (?:) para evitar nulos
                _uiState.update { it.copy(fullName = name ?: "") }
            }
        }
    }

    // --- FUNCIONES DE EDICIÓN ---

    fun onEditToggled() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }

    fun onFullNameChanged(newName: String) {
        _uiState.update { it.copy(fullName = newName) }
    }

    fun onPhoneChanged(newPhone: String) {
        _uiState.update { it.copy(phone = newPhone) }
    }

    fun onSaveChanges() {
        updateUser(_uiState.value.fullName, _uiState.value.phone)
        onEditToggled()
    }

    // --- LÓGICA DE NEGOCIO ---

    private fun updateUser(fullName: String, phone: String) {
        viewModelScope.launch {
            sessionPreferencesRepository.saveUserFullName(fullName)
            _uiState.update { it.copy(fullName = fullName, phone = phone) }
        }
    }

    // --- FUNCIÓN GPS (Ahora actualiza 'location') ---
    fun getAddressFromCoordinates(context: Context, lat: Double, long: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(lat, long, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            val addressLine = addresses[0].getAddressLine(0)
                            // Aquí actualizamos 'location'
                            _uiState.update { it.copy(location = addressLine) }
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, long, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val addressLine = addresses[0].getAddressLine(0)
                        // Aquí actualizamos 'location'
                        _uiState.update { it.copy(location = addressLine) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(location = "Ubicación no encontrada") }
            }
        }
    }
}

class ProfileViewModelFactory(
    private val userRepository: UserRepository,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(userRepository, sessionPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}