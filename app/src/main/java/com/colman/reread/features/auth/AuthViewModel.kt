package com.colman.reread.features.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.data.repository.AuthRepository
import com.colman.reread.model.User

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository.shared

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        repository.signIn(
            email = email,
            password = password,
            onSuccess = { _authState.value = AuthState.Success },
            onError = { error -> _authState.value = AuthState.Error(error) }
        )
    }

    fun signup(user: User, password: String) {
        _authState.value = AuthState.Loading
        repository.signUp(
            user,
            password,
            onSuccess = { _authState.value = AuthState.Success },
            onError = { error -> _authState.value = AuthState.Error(error) }
        )
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
