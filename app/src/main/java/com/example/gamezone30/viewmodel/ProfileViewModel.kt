package com.example.gamezone30.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamezone30.data.local.dao.entity.User
import com.example.gamezone30.data.repository.UserRepository
import com.example.gamezone30.data.session.SessionPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Locale

data class ProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val favoriteGenres: List<String> = emptyList(),
    val location: String = "",
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
            combine(
                userRepository.getUser(),
                sessionPreferencesRepository.userFullNameFlow
            ) { user: User?, fullName: String? ->
                _uiState.value = ProfileUiState(
                    fullName = user?.fullName ?: fullName ?: "",
                    email = user?.email ?: "",
                    phone = user?.phone ?: "",
                    favoriteGenres = user?.favoriteGenres ?: emptyList()
                )
            }.collect()
        }
    }

    fun onEditToggled() {
        _uiState.value = _uiState.value.copy(isEditing = !_uiState.value.isEditing)
    }

    fun onFullNameChanged(fullName: String) {
        _uiState.value = _uiState.value.copy(fullName = fullName)
    }

    fun onPhoneChanged(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone)
    }

    fun onLocationChanged(location: String) {
        _uiState.value = _uiState.value.copy(location = location)
    }

    fun getAddressFromCoordinates(context: Context, latitude: Double, longitude: Double) {
        val geocoder = Geocoder(context, Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                val address = addresses.firstOrNull()
                onLocationChanged(address?.locality ?: "")
            }
        } else {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val address = addresses?.firstOrNull()
            onLocationChanged(address?.locality ?: "")
        }
    }

    fun onSaveChanges() {
        viewModelScope.launch {
            val updatedUser = _uiState.value
            userRepository.updateUser(
                fullName = updatedUser.fullName,
                phone = updatedUser.phone
            )
            sessionPreferencesRepository.saveUserFullName(updatedUser.fullName)
            onEditToggled()
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
