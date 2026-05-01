package com.colman.reread.features.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.data.repository.UsersRepository
import com.colman.reread.model.User

class AuthViewModel : ViewModel() {

    private val repository = UsersRepository.shared

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
        repository.signIn(email, password) { uid ->
            if (uid != null) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Login failed. Check your credentials.")
            }
        }
    }

    fun signup(user: User, password: String) {
        _authState.value = AuthState.Loading
        repository.signUp(user, password) {
            _authState.value = AuthState.Success
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
