package com.ronik.geotraveljournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ronik.geotraveljournal.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application, private val authRepository: AuthRepository)
    : AndroidViewModel(application) {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val success = authRepository.register(email, password)
                if (success) {
                    _authState.value = AuthState.RegisterSuccess
                } else {
                    _authState.value = AuthState.Error("Регистрация не удалась")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Ошибка")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val success = authRepository.login(email, password)
                if (success) {
                    _authState.value = AuthState.LoginSuccess
                } else {
                    _authState.value = AuthState.Error("Логин не удался")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Ошибка")
            }
        }
    }
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object RegisterSuccess : AuthState()
    data object LoginSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}